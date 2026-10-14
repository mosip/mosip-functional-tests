# UI Test Rig Platform

## Overview

The **`uitestrig-platform`** is the parent POM for MOSIP's shared UI automation framework — Playwright
driven, Cucumber BDD, run through the JUnit5 Platform Suite engine. It builds three modules:

- **`uitest-commons`** — the core shared library: Playwright setup, Cucumber hooks, visual
  validation, reporting (Extent), and MOSIP auth utilities (reused from `apitest-commons`). Every
  UI testrig module depends on this.
- **`uitest-browserstack`** — optional BrowserStack cloud-grid integration for `uitest-commons`;
  connects Playwright via the BrowserStack CDP endpoint and reports session pass/fail plus
  video/public URLs. Activated per-project with `browserstack.enabled=yes` in `uitest.properties`.
- **`uitest-ai`** — optional AI-powered failure analysis for `uitest-commons`; diagnoses root cause
  on failing scenarios, suggests fixes, and proposes healed locators. Supports Ollama (local),
  Claude, and OpenAI as pluggable providers.

Each MOSIP module's own UI testrig (e.g. `uitest-resident` in `mosip/resident-ui`) consumes
`uitest-commons` as a dependency, and optionally `uitest-browserstack`/`uitest-ai`.

---

## Pre-requisites

- **Java 21** ([download here](https://jdk.java.net/))
- **Maven 3.9.6** or higher ([installation guide](https://maven.apache.org/install.html))
- **`apitest-commons`** — `uitest-commons` depends on it; build/install it first (see
  [`../apitest-commons/README.md`](../apitest-commons/README.md)) or resolve it from Maven Central.
- **`settings.xml`** in `.m2` ([download here](https://github.com/mosip/mosip-functional-tests/blob/master/settings.xml))

---

## Build

Build order is `uitest-commons` → `uitest-browserstack`, `uitest-ai` (parallel) — Maven resolves
this automatically from the inter-module dependencies when building from this directory:

```sh
cd mosip-functional-tests/uitestrig-platform/
mvn clean install -DskipTests -Dgpg.skip=true
```

The `maven-gpg-plugin` is bound to the `verify` phase (same as `apitest-commons`), so a plain local
install needs `-Dgpg.skip=true`. A real GPG key is only required for an actual `mvn deploy`.

---

## Using `uitest-commons` as a dependency

```xml
<dependency>
  <groupId>io.mosip.testrig.uitestrig</groupId>
  <artifactId>uitest-commons</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <scope>test</scope>
</dependency>
```

---

## License

This project is licensed under the terms of [Mozilla Public License 2.0](https://github.com/mosip/mosip-platform/blob/master/LICENSE)
