FROM mcr.microsoft.com/devcontainers/base:ubuntu-24.04

ARG ANDROID_CMDLINE_TOOLS_VERSION=11076708
ARG ANDROID_PLATFORM=android-35
ARG ANDROID_BUILD_TOOLS_LEGACY=34.0.0
ARG ANDROID_BUILD_TOOLS=35.0.0

ENV ANDROID_SDK_ROOT=/opt/android-sdk \
    ANDROID_HOME=/opt/android-sdk \
    JAVA_HOME=/opt/java/openjdk \
    PATH=/opt/android-sdk/cmdline-tools/latest/bin:/opt/android-sdk/platform-tools:${PATH}

# Android build-tools ship x86_64 AAPT2; arm64 containers need amd64 runtime libs.
RUN if [ "$(dpkg --print-architecture)" = "arm64" ]; then \
        sed -i '/^Types: deb/a Architectures: arm64' /etc/apt/sources.list.d/ubuntu.sources; \
        printf '%s\n' \
            'deb [arch=amd64 signed-by=/usr/share/keyrings/ubuntu-archive-keyring.gpg] http://archive.ubuntu.com/ubuntu/ noble main universe restricted multiverse' \
            'deb [arch=amd64 signed-by=/usr/share/keyrings/ubuntu-archive-keyring.gpg] http://archive.ubuntu.com/ubuntu/ noble-updates main universe restricted multiverse' \
            'deb [arch=amd64 signed-by=/usr/share/keyrings/ubuntu-archive-keyring.gpg] http://archive.ubuntu.com/ubuntu/ noble-backports main universe restricted multiverse' \
            'deb [arch=amd64 signed-by=/usr/share/keyrings/ubuntu-archive-keyring.gpg] http://security.ubuntu.com/ubuntu/ noble-security main universe restricted multiverse' \
            > /etc/apt/sources.list.d/ubuntu-amd64.list; \
        dpkg --add-architecture amd64; \
    fi \
    && apt-get update \
    && apt-get install -y --no-install-recommends \
        ca-certificates \
        curl \
        gh \
        $(if [ "$(dpkg --print-architecture)" = "arm64" ]; then echo "libc6:amd64 libgcc-s1:amd64 libstdc++6:amd64 zlib1g:amd64"; fi) \
        openjdk-17-jdk-headless \
        unzip \
    && mkdir -p "${ANDROID_SDK_ROOT}/cmdline-tools" /opt/java \
    && ln -s "$(dirname "$(dirname "$(readlink -f "$(command -v java)")")")" "${JAVA_HOME}" \
    && curl -fsSL "https://dl.google.com/android/repository/commandlinetools-linux-${ANDROID_CMDLINE_TOOLS_VERSION}_latest.zip" -o /tmp/android-commandlinetools.zip \
    && unzip -q /tmp/android-commandlinetools.zip -d /tmp/android-commandlinetools \
    && mv /tmp/android-commandlinetools/cmdline-tools "${ANDROID_SDK_ROOT}/cmdline-tools/latest" \
    && yes | sdkmanager --sdk_root="${ANDROID_SDK_ROOT}" \
        "platform-tools" \
        "platforms;${ANDROID_PLATFORM}" \
        "build-tools;${ANDROID_BUILD_TOOLS_LEGACY}" \
        "build-tools;${ANDROID_BUILD_TOOLS}" \
    && yes | sdkmanager --sdk_root="${ANDROID_SDK_ROOT}" --licenses >/dev/null \
    && chown -R vscode:vscode "${ANDROID_SDK_ROOT}" \
    && rm -rf /tmp/android-commandlinetools /tmp/android-commandlinetools.zip /var/lib/apt/lists/*
