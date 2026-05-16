# Emal Sa - Cloud Stream Extension

[English version](README_en.md)

## Indice

- [Plugin](#plugin)
- [Installazione](#installazione)
- [Configurazione dei plugin](#configurazione-dei-plugin)
  - [Personal Video](#personal-video)
  - [Configurazione di Personal Video](#configurazione)
  - [Catalogo richiesto](#catalogo-richiesto)
  - [Esempio locale con Simple HTTP Server](#esempio-locale-con-simple-http-server)
  - [Come funziona il provider](#come-funziona-il-provider)
- [Build locale](#build-locale)
- [Aggiungere un nuovo plugin](#aggiungere-un-nuovo-plugin)

<a id="plugin"></a>
<details open>
<summary><strong><big><big>Plugin</big></big></strong></summary>

<br>

| **Nome** | **Categorie** | **Lingua** | **Funzionante** | **Descrizione** |
|----------|:--------------:|:----------:|:---------------:|-----------------|
| Personal Video | Film | 🇺🇳 | ✅ | Legge un catalogo video pubblicato da un server HTTP/HTTPS raggiungibile dal dispositivo, sia nella rete locale sia su Internet. |

</details>

<a id="installazione"></a>
<details open>
<summary><strong><big><big>Installazione</big></big></strong></summary>

<br>

Apri CloudStream e vai in *Impostazioni* -> *Estensioni* -> *Aggiungi repository*.

Nel campo URL incolla:

```text
EmalSa
```

o:

```text
https://raw.githubusercontent.com/emal-sa/EmalSa-Cloud-Stream-Extension/main/repo.json
```

Conferma l'aggiunta della repository e attendi che CloudStream sincronizzi l'elenco delle estensioni disponibili.

</details>

<a id="configurazione-dei-plugin"></a>
<details open>
<summary><strong><big><big>Configurazione dei plugin</big></big></strong></summary>

<br>

In CloudStream le impostazioni di un'estensione si aprono da *Impostazioni* -> *Estensioni* -> nome del plugin -> *Impostazioni*. Se un plugin non mostra una schermata di impostazioni, non richiede configurazione manuale.

</details>

<a id="personal-video"></a>
<details open>
<summary><strong><big><big>Personal Video</big></big></strong></summary>

<br>

`Personal Video` legge un catalogo video pubblicato da un server HTTP/HTTPS raggiungibile dal dispositivo su cui usi CloudStream. Il server può essere nella rete locale, per esempio un PC/tablet/telefono nella stessa Wi-Fi, oppure su Internet.

Il plugin non crea il server e non carica i video: legge un file `catalog.json` dal server configurato e passa a CloudStream i link video indicati nel catalogo.

</details>

<a id="configurazione"></a>
<details open>
<summary><strong><big><big>Configurazione di Personal Video</big></big></strong></summary>

<br>

Dopo aver installato il plugin, apri CloudStream e vai in *Impostazioni* -> *Estensioni* -> `Personal Video` -> *Impostazioni*.

Nel campo `URL server HTTP` inserisci l'URL base del server che contiene il catalogo, senza `/catalog.json` alla fine. Per esempio:

```text
http://192.168.1.50:8080
```

oppure:

```text
https://video.example.com/library
```

Con un URL base come:

```text
https://video.example.com/library
```

il plugin prova a leggere:

```text
https://video.example.com/library/catalog.json
```

Non serve ricompilare il plugin quando cambia server, IP o porta: aggiorni solo questo valore nelle impostazioni di CloudStream.

</details>

<a id="catalogo-richiesto"></a>
<details open>
<summary><strong><big><big>Catalogo richiesto</big></big></strong></summary>

<br>

Il server deve pubblicare un file `catalog.json` nella root dell'URL configurato. Il catalogo contiene il nome della libreria e la lista dei contenuti da mostrare in CloudStream.

Esempio di `catalog.json`:

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

Lo stesso esempio è disponibile in `examples/PersonalVideo/catalog.local.json`.

Campi principali:

- `catalog_name`: nome della libreria mostrata dal plugin.
- `contents`: lista dei video disponibili.
- `title`: titolo del contenuto. È obbligatorio.
- `video_url`: link al file video. È obbligatorio.
- `plot`: descrizione del contenuto.
- `category`: categoria usata per raggruppare i contenuti nella home.
- `poster`: immagine di copertina.

`video_url` e `poster` possono essere:

- relativi alla root del server, come `Movies/Matrix.mp4`
- assoluti, come `https://video.example.com/library/Movies/Matrix.mp4`

I percorsi relativi sono più comodi: se cambi server, IP o porta, aggiorni solo l'URL base nelle impostazioni del plugin.

Se usi Chromecast, anche il dispositivo Chromecast deve riuscire a raggiungere i link video indicati nel catalogo.

</details>

<a id="esempio-locale-con-simple-http-server"></a>
<details open>
<summary><strong><big><big>Esempio locale con Simple HTTP Server</big></big></strong></summary>

<br>

Puoi usare l'app `Simple HTTP Server` su un dispositivo Android per pubblicare una cartella video nella rete locale.

Esempio di struttura della cartella condivisa:

```text
Dispositivo Android
└── Simple HTTP Server
    └── cartella condivisa
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

1. Sul dispositivo crea una cartella, per esempio `CloudStreamLibrary`.
2. Dentro metti `catalog.json`, le sottocartelle dei video e le locandine.
3. Avvia `Simple HTTP Server` scegliendo quella cartella come root.
4. Copia l'indirizzo mostrato dall'app, per esempio:

```text
http://192.168.1.50:8080
```

5. Inserisci quell'indirizzo nelle impostazioni di `Personal Video`, nel campo `URL server HTTP`.
6. Verifica da browser, sul dispositivo dove userai CloudStream:

```text
http://IP_DEL_TABLET:PORTA/catalog.json
```

Se il browser non apre il JSON, CloudStream non potrà leggerlo: controlla che i dispositivi siano sulla stessa rete Wi-Fi, che IP e porta siano corretti e che il server sia acceso. Se l'IP cambia spesso, conviene assegnare al dispositivo un IP fisso dal router.

</details>

<a id="come-funziona-il-provider"></a>
<details open>
<summary><strong><big><big>Come funziona il provider</big></big></strong></summary>

<br>

Il provider mostra gli elementi nella home raggruppati per `category`, abilita la ricerca per `title`/`plot`/`category` e passa a CloudStream link video HTTP diretti.

Nel caso di `Simple HTTP Server`, il dispositivo Android diventa sia catalogo sia storage video.

</details>

<a id="build-locale"></a>
<details open>
<summary><strong><big><big>Build locale</big></big></strong></summary>

<br>

Richiede JDK 17 e Android SDK.

La repository include anche un ambiente Docker già pronto in `.devcontainer/`: se usi VS Code con Dev Containers, puoi aprire il progetto nel container e lavorare con JDK 17 e Android SDK già configurati. In questo modo non devi installare manualmente gli strumenti Android sulla macchina host.

Per compilare tutti i plugin inclusi:

```sh
./gradlew make makePluginsJson
```

Per compilare solo il plugin `PersonalVideo` e rigenerare il catalogo:

```sh
./gradlew PersonalVideo:make makePluginsJson
```

</details>

<a id="aggiungere-un-nuovo-plugin"></a>
<details open>
<summary><strong><big><big>Aggiungere un nuovo plugin</big></big></strong></summary>

<br>

Questa repository è organizzata come multi-plugin CloudStream: ogni plugin vive in una cartella alla root del progetto, come `PersonalVideo`.

Per aggiungerne uno nuovo:

1. Crea una nuova cartella root con il nome del plugin, per esempio `MioPlugin`.
2. Aggiungi `MioPlugin/build.gradle.kts` con i metadata `cloudstream { ... }`.
3. Metti il codice Kotlin in `MioPlugin/src/main/kotlin/...`.
4. Aggiungi `"MioPlugin"` alla lista `include(...)` in `settings.gradle.kts`.
5. Verifica con `./gradlew projects` e poi `./gradlew make makePluginsJson`.

</details>
