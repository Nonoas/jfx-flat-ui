# jfx-flat-ui

[English](README.md) | [简体中文](README.zh-CN.md)

![Java](https://img.shields.io/badge/Java-11%2B-2F81F7?logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-17.0.0.1-FF6F00)
![Maven](https://img.shields.io/badge/Maven-io.github.nonoas%3Ajfx--flat--ui-C71A36?logo=apachemaven&logoColor=white)
![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)

一个面向 JavaFX 的轻量级扁平化组件库，重点覆盖主题切换、自定义桌面窗口能力，以及一组可复用的 UI 工具类。

当前项目主要提供：

- 明暗两套主题样式表
- 语义化 CSS 样式类与样式工具方法
- `Switch`、`Card`、`SVGButton`、`PopupTextField` 等自定义控件
- `AppStage`、`TransparentPane`、`ToastQueue` 等桌面窗口辅助能力
- 配置管理、异步任务、资源生命周期管理等通用工具类

## 目录

- [项目特性](#项目特性)
- [环境要求](#环境要求)
- [安装方式](#安装方式)
- [快速开始](#快速开始)
- [核心组件](#核心组件)
- [主题与样式](#主题与样式)
- [桌面窗口能力](#桌面窗口能力)
- [工具类](#工具类)
- [项目结构](#项目结构)
- [参与贡献](#参与贡献)
- [许可证](#许可证)

## 项目特性

- 为 JavaFX 提供统一的 Flat UI 风格
- 内置 `LightTheme` 与 `DarkTheme`
- 通过 `Styles` 提供语义化样式标识，例如 `accent`、`success`、`danger`、`rounded`、`elevated-*`
- 提供带动画效果的 `Switch` 开关控件，支持 CSS 配置标签位置
- 提供适合设置页、仪表盘卡片区的 `Card` 组件
- 提供基于 SVG 的按钮与图标节点
- 提供适合桌面应用的无边框窗口封装，支持拖动、缩放、最大化和系统按钮
- 提供轻量级 Toast 队列提示
- 提供后台任务、配置存储、资源自动释放等工程化辅助能力

## 环境要求

- JDK `11+`
- JavaFX `17.0.0.1`
- Maven 或 Gradle

说明：

- 当前库以 Java 11 目标版本编译。
- 你的业务应用仍需要为自身运行平台正确配置 JavaFX 运行时依赖。

## 安装方式

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

## 快速开始

当前仓库没有附带独立 demo 应用，下面给出一个最小可运行的接入示例。

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
                "用于构建扁平化 JavaFX 界面的组件与窗口工具。",
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

## 核心组件

| 组件 | 包路径 | 说明 |
| --- | --- | --- |
| `Switch` | `...control` | 带动画效果的双态开关控件，支持 `ToggleGroup` |
| `Card` | `...control` | 轻量卡片组件，支持标题、描述和图形节点 |
| `SVGButton` | `...control` | 支持 SVG 图形、悬停颜色和背景切换的按钮 |
| `PopupTextField` | `...control` | 带弹出内容区域的输入框，可用于建议列表或搜索提示 |
| `AlignedTableColumn` | `...control` | 支持表头和单元格对齐方式配置的表格列 |
| `SVGImage` | `...pane` | 基于 SVGPath 的图标/图像节点，支持尺寸和填充色控制 |
| `JustifiedFlowPane` | `...pane` | 将子项按等宽行进行自适应排布的布局容器 |

## 主题与样式

项目内置两套主题：

- `LightTheme`
- `DarkTheme`

同时，`github.nonoas.jfx.flat.ui.theme.Styles` 暴露了大量语义化样式常量。

常见写法如下：

```java
button.getStyleClass().addAll(Styles.ACCENT, Styles.ROUNDED);
deleteButton.getStyleClass().addAll(Styles.DANGER, Styles.BUTTON_OUTLINED);
textField.getStyleClass().add(Styles.LARGE);
container.getStyleClass().addAll(Styles.BG_SUBTLE, Styles.ELEVATED_1);
```

常用样式类别包括：

- 状态与语义：`ACCENT`、`SUCCESS`、`WARNING`、`DANGER`
- 形态与密度：`ROUNDED`、`SMALL`、`MEDIUM`、`LARGE`、`DENSE`
- 背景与边框：`BG_DEFAULT`、`BG_SUBTLE`、`BORDER_DEFAULT`
- 阴影层级：`ELEVATED_1` 到 `ELEVATED_4`
- 按钮变体：`BUTTON_ICON`、`BUTTON_CIRCLE`、`BUTTON_OUTLINED`
- 文本辅助：`TITLE_1` 到 `TITLE_4`、`TEXT_MUTED`、`TEXT_SMALL`

对于更细粒度的样式操作，还可以使用 `Styles` 中的工具方法：

- `toggleStyleClass(...)`
- `addStyleClass(...)`
- `activatePseudoClass(...)`
- `appendStyle(...)`
- `removeStyle(...)`
- `toDataURI(...)`

## 桌面窗口能力

`AppStage` 对透明 `Stage` 做了桌面应用层面的封装，主要包括：

- 透明窗口根容器与圆角阴影
- 内置最小化、最大化、关闭按钮
- 通过 `registryDragger(...)` 实现拖动标题栏移动窗口
- 针对无边框窗口的手动缩放处理
- 通过 `setContentView(...)` 挂载自定义内容

最小示例：

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

轻量消息提示可以直接使用：

```java
ToastQueue.show(stage, "Operation completed", 2000);
```

## 工具类

### 异步任务

```java
new TaskHandler<String>()
        .whenCall(() -> loadDataFromService())
        .andThen(result -> resultLabel.setText(result))
        .handle();
```

### 配置存储

```java
ConfigManager config = new ConfigManager("settings", "MyDesktopApp");
config.set("theme", "dark");
config.saveConfig();
```

默认情况下，配置会保存在用户主目录下的应用专属隐藏目录中。

### 资源生命周期管理

如果你希望在 JavaFX 应用退出时自动释放资源，可以继承 `AutoReleaseApplication`，并通过 `ResourceManager` 注册资源。

```java
ResourceManager.getInstance().register(() -> socket.close());
```

## 项目结构

```text
src/main/java/github/nonoas/jfx/flat/ui
├─ control      # Switch、Card、SVGButton 等自定义控件
├─ pane         # 布局容器与 SVG 视觉节点
├─ stage        # 窗口封装与 Toast 工具
├─ theme        # 主题接口、主题实现与样式常量
├─ config       # 配置持久化工具
├─ concurrent   # 异步任务工具
└─ utils        # 通用 UI 与颜色工具
```

## 参与贡献

欢迎提交 Issue 与 Pull Request。

建议的贡献流程：

1. Fork 仓库。
2. 创建功能分支。
3. 保持变更聚焦且有清晰说明。
4. 如果改动影响 API 行为，补充或更新示例。
5. 提交 PR，并说明动机、实现方式与影响范围。

## 许可证

本项目基于 [Apache License 2.0](LICENSE) 开源。
