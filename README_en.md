# Emal Sa - Cloud Stream Extension

[Italian version](README.md)

## Table of Contents

- [Plugin](#plugin)
- [Installation](#installation)
- [Plugin Configuration](#plugin-configuration)
  - [Personal Video](#personal-video)
  - [Personal Video Configuration](#personal-video-configuration)
  - [Required Catalog](#required-catalog)
  - [Local Example with Simple HTTP Server](#local-example-with-simple-http-server)
  - [How the Provider Works](#how-the-provider-works)
- [Local Build](#local-build)
- [Adding a New Plugin](#adding-a-new-plugin)

<a id="plugin"></a>
<details open>
<summary><strong><big><big>Plugin</big></big></strong></summary>

<br>

| **Name** | **Categories** | **Language** | **Working** | **Description** |
|----------|:--------------:|:------------:|:-----------:|-----------------|
| Personal Video | Movies | 🇺🇳 | ✅ | Reads a video catalog published by an HTTP/HTTPS server reachable from the device, either on the local network or on the Internet. |

</details>

<a id="installation"></a>
<details open>
<summary><strong><big><big>Installation</big></big></strong></summary>

<br>

Open CloudStream and go to *Settings* -> *Extensions* -> *Add repository*.

Paste this into the URL field:

```text
EmalSa
```

or:

```text
https://raw.githubusercontent.com/emal-sa/EmalSa-Cloud-Stream-Extension/main/repo.json
```

Confirm the repository addition and wait for CloudStream to sync the list of available extensions.

</details>

<a id="plugin-configuration"></a>
<details open>
<summary><strong><big><big>Plugin Configuration</big></big></strong></summary>

<br>

In CloudStream, an extension's settings can be opened from *Settings* -> *Extensions* -> plugin name -> *Settings*. If a plugin does not show a settings screen, it does not require manual configuration.

</details>

<a id="personal-video"></a>
<details open>
<summary><strong><big><big>Personal Video</big></big></strong></summary>

<br>

`Personal Video` reads a video catalog published by an HTTP/HTTPS server reachable from the device where you use CloudStream. The server can be on the local network, for example a PC/tablet/phone on the same Wi-Fi, or on the Internet.

The plugin does not create the server and does not upload videos: it reads a `catalog.json` file from the configured server and passes the video links from the catalog to CloudStream.

</details>

<a id="personal-video-configuration"></a>
<details open>
<summary><strong><big><big>Personal Video Configuration</big></big></strong></summary>

<br>

After installing the plugin, open CloudStream and go to *Settings* -> *Extensions* -> `Personal Video` -> *Settings*.

In the `URL server HTTP` field, enter the base URL of the server that contains the catalog, without `/catalog.json` at the end. For example:

```text
http://192.168.1.50:8080
```

or:

```text
https://video.example.com/library
```

With a base URL like:

```text
https://video.example.com/library
```

the plugin tries to read:

```text
https://video.example.com/library/catalog.json
```

You do not need to rebuild the plugin when the server, IP, or port changes: just update this value in the CloudStream settings.

</details>

<a id="required-catalog"></a>
<details open>
<summary><strong><big><big>Required Catalog</big></big></strong></summary>

<br>

The server must publish a `catalog.json` file at the root of the configured URL. The catalog contains the library name and the list of contents to show in CloudStream.

Example `catalog.json`:

```json
{
  "catalog_name": "Personal Video",
  "contents": [
    {
      "title": "Matrix",
      "plot": "A programmer discovers the truth about his world.",
      "category": "Movies",
      "poster": "Poster/matrix.jpg",
      "video_url": "Movies/Matrix.mp4"
    },
    {
      "title": "Example S01E01",
      "plot": "First episode.",
      "category": "Series",
      "poster": "Poster/example.jpg",
      "video_url": "Series/Example S01E01.mp4"
    }
  ]
}
```

The same example is available in `examples/PersonalVideo/catalog.local.json`.

Main fields:

- `catalog_name`: name of the library shown by the plugin.
- `contents`: list of available videos.
- `title`: content title. Required.
- `video_url`: link to the video file. Required.
- `plot`: content description.
- `category`: category used to group contents on the home page.
- `poster`: cover image.

`video_url` and `poster` can be:

- relative to the server root, such as `Movies/Matrix.mp4`
- absolute, such as `https://video.example.com/library/Movies/Matrix.mp4`

Relative paths are more convenient: if you change server, IP, or port, you only need to update the base URL in the plugin settings.

If you use Chromecast, the Chromecast device must also be able to reach the video links listed in the catalog.

</details>

<a id="local-example-with-simple-http-server"></a>
<details open>
<summary><strong><big><big>Local Example with Simple HTTP Server</big></big></strong></summary>

<br>

You can use the `Simple HTTP Server` app on an Android device to publish a video folder on the local network.

Example shared folder structure:

```text
Android device
└── Simple HTTP Server
    └── shared folder
        ├── catalog.json
        ├── Movies/
        │   ├── Matrix.mp4
        │   └── Inception.mp4
        ├── Series/
        │   └── Example S01E01.mp4
        └── Poster/
            ├── matrix.jpg
            └── inception.jpg
```

1. On the device, create a folder, for example `CloudStreamLibrary`.
2. Put `catalog.json`, the video subfolders, and the poster images inside it.
3. Start `Simple HTTP Server` and choose that folder as the root.
4. Copy the address shown by the app, for example:

```text
http://192.168.1.50:8080
```

5. Enter that address in the `Personal Video` settings, in the `URL server HTTP` field.
6. Check it from a browser on the device where you will use CloudStream:

```text
http://TABLET_IP:PORT/catalog.json
```

If the browser does not open the JSON file, CloudStream will not be able to read it: check that the devices are on the same Wi-Fi network, that the IP and port are correct, and that the server is running. If the IP changes often, it is better to assign the device a fixed IP from the router.

</details>

<a id="how-the-provider-works"></a>
<details open>
<summary><strong><big><big>How the Provider Works</big></big></strong></summary>

<br>

The provider shows items on the home page grouped by `category`, enables search by `title`/`plot`/`category`, and passes direct HTTP video links to CloudStream.

When using `Simple HTTP Server`, the Android device acts as both the catalog and the video storage.

</details>

<a id="local-build"></a>
<details open>
<summary><strong><big><big>Local Build</big></big></strong></summary>

<br>

Requires JDK 17 and Android SDK.

The repository also includes a ready-to-use Docker environment in `.devcontainer/`: if you use VS Code with Dev Containers, you can open the project inside the container and work with JDK 17 and Android SDK already configured. This way you do not need to manually install the Android tools on the host machine.

To build all included plugins:

```sh
./gradlew make makePluginsJson
```

To build only the `PersonalVideo` plugin and regenerate the catalog:

```sh
./gradlew PersonalVideo:make makePluginsJson
```

</details>

<a id="adding-a-new-plugin"></a>
<details open>
<summary><strong><big><big>Adding a New Plugin</big></big></strong></summary>

<br>

This repository is organized as a multi-plugin CloudStream repository: each plugin lives in a folder at the project root, such as `PersonalVideo`.

To add a new one:

1. Create a new root folder with the plugin name, for example `MyPlugin`.
2. Add `MyPlugin/build.gradle.kts` with the `cloudstream { ... }` metadata.
3. Put the Kotlin code in `MyPlugin/src/main/kotlin/...`.
4. Add `"MyPlugin"` to the `include(...)` list in `settings.gradle.kts`.
5. Check with `./gradlew projects`, then run `./gradlew make makePluginsJson`.

</details>
