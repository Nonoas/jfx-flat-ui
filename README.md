# jfx-flat-ui

[English](README.md) | [简体中文](README.zh-CN.md)

![Java](https://img.shields.io/badge/Java-11%2B-2F81F7?logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-17.0.0.1-FF6F00)
![Maven](https://img.shields.io/badge/Maven-io.github.nonoas%3Ajfx--flat--ui-C71A36?logo=apachemaven&logoColor=white)
![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)

A lightweight JavaFX component library focused on flat visual style, theme switching, custom desktop window helpers, and reusable UI utilities.

It currently provides:

- Light and dark theme stylesheets
- Semantic CSS style classes and helper utilities
- Custom controls such as `Switch`, `Card`, `SVGButton`, and `PopupTextField`
- Desktop window helpers such as `AppStage`, `TransparentPane`, and `ToastQueue`
- Common utility classes for config, async tasks, and resource lifecycle management

## Table of Contents

- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [Core Components](#core-components)
- [Theme and Styling](#theme-and-styling)
- [Desktop Window Helpers](#desktop-window-helpers)
- [Utilities](#utilities)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)

## Features

- Flat UI styling for JavaFX controls and custom components
- Built-in `LightTheme` and `DarkTheme`
- Semantic style tokens via `Styles`, such as `accent`, `success`, `danger`, `rounded`, and `elevated-*`
- Custom switch control with animated thumb and CSS-configurable label position
- Card-style control for quick dashboard and settings layouts
- SVG-based button and icon support
- Custom borderless desktop window wrapper with drag, resize, maximize, and system buttons
- Toast notification queue for lightweight feedback
- Helper classes for async background tasks, config storage, and automatic resource cleanup

## Requirements

- JDK `11+`
- JavaFX `17.0.0.1`
- Maven or Gradle

Notes:

- The library is compiled with Java 11 target compatibility.
- Your application should still configure JavaFX runtime dependencies for its own target platform.

## Installation

### Maven

```xml
<dependency>
    <groupId>io.github.nonoas</groupId>
    <artifactId>jfx-flat-ui</artifactId>
    <version>1.0.3</version>
</dependency>
```

### Gradle

```gradle
implementation("io.github.nonoas:jfx-flat-ui:1.0.3")
```

## Quick Start

The repository does not currently ship with a standalone demo app, so the snippet below is a minimal integration example.

```java
import github.nonoas.jfx.flat.ui.control.Card;
import github.nonoas.jfx.flat.ui.control.SVGPath;
import github.nonoas.jfx.flat.ui.control.Switch;
import github.nonoas.jfx.flat.ui.pane.SVGImage;
import github.nonoas.jfx.flat.ui.stage.ToastQueue;
import github.nonoas.jfx.flat.ui.theme.DarkTheme;
import github.nonoas.jfx.flat.ui.theme.LightTheme;
import github.nonoas.jfx.flat.ui.theme.Styles;
import github.nonoas.jfx.flat.ui.theme.Theme;
import java.util.Objects;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class DemoApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        applyTheme(new LightTheme());

        var icon = new SVGImage(SVGPath.SETTING_BUTTON.value(), Color.web("#0969da"));
        icon.setSize(18);

        var card = new Card(
                "JFX Flat UI",
                "Flat-styled JavaFX controls and window helpers.",
                icon
        );

        var themeSwitch = new Switch("Dark mode");

        var toastButton = new Button("Show toast");
        toastButton.getStyleClass().addAll(Styles.ACCENT, Styles.ROUNDED);
        toastButton.setOnAction(e -> ToastQueue.show(primaryStage, "Saved successfully", 2000));

        themeSwitch.selectedProperty().addListener((obs, oldValue, dark) -> {
            applyTheme(dark ? new DarkTheme() : new LightTheme());
        });

        var root = new VBox(16, card, themeSwitch, toastButton);
        root.setPadding(new Insets(24));

        primaryStage.setScene(new Scene(root, 640, 360));
        primaryStage.setTitle("jfx-flat-ui Demo");
        primaryStage.show();
    }

    private void applyTheme(Theme theme) {
        String stylesheet = Objects.requireNonNull(
                getClass().getResource(theme.getUserAgentStylesheet())
        ).toExternalForm();
        Application.setUserAgentStylesheet(stylesheet);
    }
}
```

## Core Components

| Component | Package | Description |
| --- | --- | --- |
| `Switch` | `...control` | Animated two-state toggle control with CSS support and `ToggleGroup` integration |
| `Card` | `...control` | Lightweight card component with title, description, and graphic |
| `SVGButton` | `...control` | Button with SVG graphic and hover color/background builder |
| `PopupTextField` | `...control` | Text field with popup suggestion/content area |
| `AlignedTableColumn` | `...control` | Table column with configurable header and cell alignment |
| `SVGImage` | `...pane` | SVG-based icon/image node with size and fill control |
| `JustifiedFlowPane` | `...pane` | Responsive pane that lays out items in evenly distributed rows |

## Theme and Styling

Two built-in themes are included:

- `LightTheme`
- `DarkTheme`

The CSS layer also exposes semantic style classes through `github.nonoas.jfx.flat.ui.theme.Styles`.

Common examples:

```java
button.getStyleClass().addAll(Styles.ACCENT, Styles.ROUNDED);
deleteButton.getStyleClass().addAll(Styles.DANGER, Styles.BUTTON_OUTLINED);
textField.getStyleClass().add(Styles.LARGE);
container.getStyleClass().addAll(Styles.BG_SUBTLE, Styles.ELEVATED_1);
```

Useful style categories include:

- State and intent: `ACCENT`, `SUCCESS`, `WARNING`, `DANGER`
- Shape and density: `ROUNDED`, `SMALL`, `MEDIUM`, `LARGE`, `DENSE`
- Surfaces: `BG_DEFAULT`, `BG_SUBTLE`, `BORDER_DEFAULT`
- Elevation: `ELEVATED_1` to `ELEVATED_4`
- Button variants: `BUTTON_ICON`, `BUTTON_CIRCLE`, `BUTTON_OUTLINED`
- Text helpers: `TITLE_1` to `TITLE_4`, `TEXT_MUTED`, `TEXT_SMALL`

For advanced styling, `Styles` also includes helpers such as:

- `toggleStyleClass(...)`
- `addStyleClass(...)`
- `activatePseudoClass(...)`
- `appendStyle(...)`
- `removeStyle(...)`
- `toDataURI(...)`

## Desktop Window Helpers

`AppStage` wraps a transparent JavaFX `Stage` and adds a desktop-app oriented shell:

- Transparent window root with rounded corners
- Built-in minimize, maximize, and close buttons
- Window drag support via `registryDragger(...)`
- Manual resize handling for borderless windows
- Optional custom content via `setContentView(...)`

Minimal usage:

```java
import github.nonoas.jfx.flat.ui.stage.AppStage;
import javafx.scene.Parent;

Parent root = createRootView();
Parent titleBar = createTitleBar();

AppStage appStage = new AppStage();
appStage.setTitle("My Desktop App");
appStage.setSize(960, 640);
appStage.setMinWidth(720);
appStage.setMinHeight(480);
appStage.setContentView(root);
appStage.registryDragger(titleBar);
appStage.show();
```

For lightweight notifications, use:

```java
ToastQueue.show(stage, "Operation completed", 2000);
```

## Utilities

### Async tasks

```java
new TaskHandler<String>()
        .whenCall(() -> loadDataFromService())
        .andThen(result -> resultLabel.setText(result))
        .handle();
```

### Config storage

```java
ConfigManager config = new ConfigManager("settings", "MyDesktopApp");
config.set("theme", "dark");
config.saveConfig();
```

This stores data under a hidden app-specific directory in the user home by default.

### Resource lifecycle

If you want resources to be released automatically when the JavaFX application stops, extend `AutoReleaseApplication` and register resources through `ResourceManager`.

```java
ResourceManager.getInstance().register(() -> socket.close());
```

## Project Structure

```text
src/main/java/github/nonoas/jfx/flat/ui
├─ control      # custom controls such as Switch, Card, SVGButton
├─ pane         # layout and SVG-based visual nodes
├─ stage        # window shell and toast helpers
├─ theme        # theme contracts, themes, and style constants
├─ config       # config persistence helper
├─ concurrent   # async task helper
└─ utils        # shared UI and color utilities
```

## Contributing

Issues and pull requests are welcome.

Recommended contribution flow:

1. Fork the repository.
2. Create a feature branch.
3. Keep changes focused and documented.
4. Add or update examples when API behavior changes.
5. Open a pull request with a clear summary of motivation and impact.

## License

This project is licensed under the [Apache License 2.0](LICENSE).
