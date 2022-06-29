# Multi State Switch  [![](https://jitpack.io/v/davidmigloz/multi-state-switch.svg)](https://jitpack.io/#davidmigloz/multi-state-switch)

Android library that provides a multi state switch view.

![screenshot](docs/multi-state-switch.gif)

## Usage

Take a look at [the sample app](https://github.com/davidmigloz/multi-state-switch/tree/master/sample) to see a live example of the capabilities of the library.

#### Step 1

Add the JitPack repository to your `build.gradle ` file:

```gradle
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
```

#### Step 2

Add the dependency:

```gradle
dependencies {
	compile 'com.github.davidmigloz:multi-state-switch:2.0.1'
}
```

[CHANGELOG](https://github.com/davidmigloz/multi-state-switch/blob/master/CHANGELOG.md)

#### Step 3

Use `MultiStateSwitch` view in your layout:

```xml
<com.davidmiguel.multistateswitch.MultiStateSwitch
    android:id="@+id/switch"
    ... />
```

Add states:

```kotlin
switch.addStatesFromStrings(listOf("One", "Two", "Three"))
```

Use `StateStyle` to customize the style of the states:

```kotlin
switch.addStateFromString(
        "Cold",
        StateStyle.Builder()
                .withSelectedBackgroundColor(Color.BLUE)
                .withDisabledTextColor(Color.WHITE)
                .build()
)
```

Use `State` object if you need to display different text depending on the state's state:

```kotlin
switch.addState(
    State(
        text = "ON",
        selectedText = "OFF",
        disabledText = "OFF"
    )
)
```

#### Attributes

- `app:multistateswitch_background_color=[color|reference]`
- `app:multistateswitch_background_radius=[float|reference]`
- `app:multistateswitch_text_color=[color|reference]`
- `app:multistateswitch_text_size=[dimension|reference]`
- `app:multistateswitch_selected_state_index=[integer|reference]`
- `app:multistateswitch_selected_background_color=[color|reference]`
- `app:multistateswitch_selected_text_color=[color|reference]`
- `app:multistateswitch_selected_text_size=[dimension|reference]`
- `app:multistateswitch_disabled_state_enabled=[boolean|reference]`
- `app:multistateswitch_disabled_state_index=[integer|reference]`
- `app:multistateswitch_disabled_background_color=[color|reference]`
- `app:multistateswitch_disabled_text_color=[color|reference]`
- `app:multistateswitch_disabled_text_size=[dimension|reference]`
- `app:multistateswitch_max_number_states=[integer|reference]`

#### API

- `addState(state: State, stateStyle: StateStyle? = null)`: adds state to the switch.
- `addStates(states: List<State>, stateStyles: List<StateStyle>? = null)`: adds states to the switch and the displaying styles. If you provide styles, you have to provide them for every state.
- `addStateFromString(stateText: String, stateStyle: StateStyle? = null)`: adds state to the switch directly from a string. The text will be used for normal, selected and disabled state.
- `addStatesFromStrings(statesTexts: List<String>, stateStyles: List<StateStyle>? = null)`: adds states to the switch directly from a string and the displaying styles. If you provide styles, you have to provide them for every state. The texts will be used for normal, selected and disabled states.
- `replaceState(stateIndex: Int, state: State, stateStyle: StateStyle? = null)`: replaces state.
- `replaceStateFromString(stateIndex: Int, stateText: String)`: replaces state directly from a string. The text will be used for normal, selected and disabled states.
- `removeState(stateIndex: Int)`: removes an state.
- `selectState(index: Int, notifyStateListeners: Boolean = true)`: selects state in given index. If `notifyStateListeners` is `true` all the listeners will be notified about the new selected state.
- `getNumberStates(): Int`: returns number of states of the switch.
- `setMaxNumberStates(maxNumberStates: Int)`: Sets the max number of states. If you try to add a new state but the number of states is already `maxNumberStates` the state will be ignored. By default is `-1` which means that there is no restriction. This parameter is also used to determine how many states to show in the editor preview. If it is set to no limit, `3` will be rendered by default, if not the number of states drawn will match `maxNumberStates`.
- `getMaxNumberStates(): Int`: returns max number of states. By default is -1 which means that there is no restriction.
- `hasMaxNumberStates(): Boolean`: checks whether there is a limit in the number of states or not.
- `setTextTypeface(textTypeface: Typeface)`: sets typeface.
- `setPadding(left: Int, top: Int, right: Int, bottom: Int)`


#### Listener

To listen to state changes, you have to register a `StateListener`:

```kotlin
binding.defaultSwitch.addStateListener { stateIndex, state ->
    // ...
}
```

## Contributing

If you find any issues or you have any questions, ideas... feel free to [open an issue](https://github.com/davidmigloz/multi-state-switch/issues/new).
Pull request are very appreciated.

## License

Copyright (c) 2022 David Miguel Lozano

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
