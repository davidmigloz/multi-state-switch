# Multi State Switch  [![](https://jitpack.io/v/davidmigloz/multi-state-switch.svg)](https://jitpack.io/#davidmigloz/multi-state-switch)

Android library that provides a multi state switch view.

![screenshot](docs/multi-state-switch.gif)

## Usage

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
	compile 'com.github.davidmigloz:multi-state-switch:1.2.2'
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

TODO

#### Attributes

TODO

#### API

TODO

#### Callback

TODO

## Contributing

If you find any issues or you have any questions, ideas... feel free to [open an issue](https://github.com/davidmigloz/multi-state-switch/issues/new).
Pull request are very appreciated.

## License

Copyright (c) 2018 David Miguel Lozano

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
