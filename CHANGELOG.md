# Changelog

## `2.0.1` (23/03/21)

- Listener called several times when dragging a state #10
- NestedScrollView not considered as a VerticalScrollingContainer #11

## `2.0.0` (22/03/21)

- Support remove states programmatically #6
- Support nested scroll when used inside a RecyclerView or ViewPager #8
- Migrate to Kotlin #9
- Update dependencies

**Breaking change:**
- Minimum API requirements: API >= 21 (Android 5.0 - LOLLIPOP)
- `replaceState(int stateIndex, String stateText)` renamed to `replaceStateFromString(stateIndex: Int, stateText: String)`

## `1.4.1` (10/08/19)

- Prevent crash when OutOfMemoryError is thrown while creating bitmaps #4

## `1.4.0` (17/12/18)

- Fix NPE when replacing state before view being measured #3
- Add max number of states #1

## `1.3.1` (14/12/18)

- Fix NPE when trying to create state bitmap #2

## `1.3.0` (11/12/18)

- Add extra methods to add states from strings.
- Fix wrong type for disabled_text_color attribute.

## `1.2.1` (10/12/18)

- Prevent NPE when selecting state if view not initialised.

## `1.2.0` (10/12/18)

- Allow replacing state directly from a string.

## `1.1.0` (10/12/18)

- Allow replacing state without styles.
- Allow adding state directly from a string.
- Allow not to notify listeners when selecting state.

## `1.0.0` (09/12/18)

- Initial release.
