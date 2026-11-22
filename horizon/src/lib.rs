//! Clumps for Horizon: merges nearby experience orbs regardless of value and removes XP pickup cooldown.

use std::collections::HashMap;
use std::sync::{LazyLock, Mutex};

use mod_api::runtime;
use mod_api::{
    self, ExperienceOrbCanMergeEvent, ExperienceOrbMergeEvent, Hooks, PickupXpEvent, ServerHost,
};

const MAX_ORB_VALUE: i64 = 32_767;

/// Remainder XP (mod 32767) for orbs whose combined XP exceeds `i16::MAX`.
static REMAINDERS: LazyLock<Mutex<HashMap<(String, u32), i64>>> =
    LazyLock::new(|| Mutex::new(HashMap::new()));

fn take_remainder(dimension: &str, orb: u32) -> i64 {
    if orb == 0 {
        return 0;
    }
    REMAINDERS
        .lock()
        .unwrap_or_else(|e| e.into_inner())
        .remove(&(dimension.to_string(), orb))
        .unwrap_or(0)
}

fn set_remainder(dimension: &str, orb: u32, rem: i64) {
    let mut map = REMAINDERS.lock().unwrap_or_else(|e| e.into_inner());
    if rem > 0 {
        map.insert((dimension.to_string(), orb), rem);
    } else {
        map.remove(&(dimension.to_string(), orb));
    }
}

/// Computes `(value, count, remainder)` for a merged orb of `total` XP.
fn split_merged_xp(total: i64) -> (i32, u32, i64) {
    if total <= 0 {
        return (1, 1, 0);
    }
    if total <= MAX_ORB_VALUE {
        return (total as i32, 1, 0);
    }
    let count = (total / MAX_ORB_VALUE).min(i32::MAX as i64);
    let rem = total - count * MAX_ORB_VALUE;
    (MAX_ORB_VALUE as i32, count as u32, rem)
}

#[derive(Default)]
struct ClumpsMod;

#[mod_api::horizon_mod]
impl Hooks for ClumpsMod {
    /// Allow any two experience orbs in reach to merge, regardless of value or entity ID.
    fn experience_orb_can_merge(
        &self,
        _host: &mut dyn ServerHost,
        event: &mut ExperienceOrbCanMergeEvent<'_>,
    ) {
        event.can_merge = true;
    }

    /// Combine XP totals accurately when orbs of different values (or with remainders) merge.
    fn experience_orb_merge(
        &self,
        host: &mut dyn ServerHost,
        event: &mut ExperienceOrbMergeEvent<'_>,
    ) -> runtime::Flow {
        let into_rem = take_remainder(event.dimension, event.into);
        let from_rem = take_remainder(event.dimension, event.from);

        if event.into_value == event.from_value && into_rem == 0 && from_rem == 0 {
            return runtime::Flow::Continue;
        }

        let into_total = (event.into_value as i64) * (event.into_count as i64) + into_rem;
        let from_total = (event.from_value as i64) * (event.from_count as i64) + from_rem;
        let total = into_total.saturating_add(from_total);

        let (value, count, rem) = split_merged_xp(total);
        if host.set_experience_orb(event.dimension, event.into, Some(value), Some(count), None) {
            set_remainder(event.dimension, event.into, rem);
        } else {
            // Restore remainders if the engine refused the write.
            set_remainder(event.dimension, event.into, into_rem);
            set_remainder(event.dimension, event.from, from_rem);
        }

        runtime::Flow::Continue
    }

    /// Remove XP pickup cooldown and award any remainder XP above `32767 * count`.
    fn pickup_xp(
        &self,
        host: &mut dyn ServerHost,
        event: &mut PickupXpEvent<'_>,
    ) -> runtime::Flow {
        let rem = take_remainder(event.dimension, event.orb);
        if let Some(player) = host.player(event.player) {
            let _ = player.set_exp_cooldown(host, 0);
            if rem > 0 {
                let _ = player.give_experience_points(host, rem.min(u32::MAX as i64) as u32);
            }
        }
        runtime::Flow::Continue
    }
}

#[cfg(test)]
mod tests {
    use super::split_merged_xp;

    #[test]
    fn small_totals_fit_in_single_orb_value() {
        assert_eq!(split_merged_xp(12), (12, 1, 0));
        assert_eq!(split_merged_xp(32_767), (32_767, 1, 0));
    }

    #[test]
    fn large_totals_preserve_exact_remainder() {
        let (value, count, rem) = split_merged_xp(70_000);
        assert_eq!((value, count, rem), (32_767, 2, 4_466));
        assert_eq!((value as i64) * (count as i64) + rem, 70_000);
    }
}
