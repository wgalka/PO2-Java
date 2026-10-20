---
layout: default
title: "Środowisko laboratoriów — IntelliJ IDEA"
---

# Środowisko laboratoriów — IntelliJ IDEA

Projekty należy konfigurować w IntelliJ IDEA z użyciem Mavena. Poniżej podano
konkretne stabilne wersje JDK i JavaFX obowiązujące w tej edycji instrukcji.

Stan zweryfikowany **13 października 2026**:

| Element | Wersja w laboratoriach |
|---|---|
| JDK | **27 (GA)** |
| JavaFX | **27** — wszystkie moduły `org.openjfx` |
| Maven `maven.compiler.release` | **27** |
| IntelliJ Project SDK, Module SDK, Maven Runner i Importer | **JDK 27** |
| Language level | **27**, bez preview |

Nie należy instalować wydań EA, alpha, beta, RC ani SNAPSHOT bibliotek ani
włączać `--enable-preview` lub używać API inkubacyjnych. Oznaczenie `1.0-SNAPSHOT`
własnego projektu w przykładowym POM jest numerem roboczym aplikacji studenta,
nie wersją JDK ani zależnością prerelease.

JavaFX 27 wymaga co najmniej JDK 25; przyjęty tu JDK 27 spełnia ten warunek.
Numerów JDK i JavaFX nie dobiera się na podstawie samej równości numerów.
We wszystkich modułach projektu należy używać JDK 27 GA i JavaFX 27.
Wersji narzędzi nie należy zmieniać ani zastępować dynamiczną wersją `LATEST`.

Źródła wersji: [wydania GA JDK 27](https://www.oracle.com/java/technologies/javase/27u-relnotes.html)
oraz [wydanie i wymagania JavaFX 27](https://gluonhq.com/products/javafx/openjfx-27-release-notes/).

## Pierwszy projekt

1. Utworzyć projekt przez **New Project → Java**, nadać mu nazwę `lab1` i wybrać **Maven** jako system budowania.
2. W polu JDK wskazać JDK 27 GA albo użyć **Download JDK → Version 27** i wybrać wydanie bez oznaczenia Early Access. Środowisko uruchomieniowe IDE nie zastępuje JDK projektu.
3. Ustawić GroupId `pl.uczelnia` i ArtifactId `lab1`. W kolejnych projektach zmieniać numer laboratorium.
4. Otworzyć `pom.xml` i zastosować poniższy szablon. Zależności z instrukcji umieszczać w `dependencies`, a pluginy w `build/plugins`.
5. Po zmianie POM zsynchronizować Maven przez **Load Maven Changes / Reload All Maven Projects**. Nazwa polecenia zależy od wersji IDE.
6. W `src/main/java` utworzyć pakiet `pl.uczelnia.lab1` i klasę `Main`. Pierwszą linią pliku powinno być `package pl.uczelnia.lab1;`.
7. Uruchomić `main` zieloną strzałką przy metodzie. Komunikaty i wyjątki są widoczne w konsoli **Run**.

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>pl.uczelnia</groupId>
    <artifactId>lab1</artifactId>
    <version>1.0-SNAPSHOT</version>
    <properties>
        <maven.compiler.release>27</maven.compiler.release>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    <dependencies>
        <!-- Tutaj zależności z danej instrukcji. Swing nie wymaga zależności. -->
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.13.0</version>
            </plugin>
        </plugins>
    </build>
</project>
```

Fragmenty XML w laboratoriach uzupełniają ten plik; nie należy wklejać drugiego
`project`, `build` ani `dependencies`. Przykłady pojedynczego listenera lub metody
należy umieszczać w istniejącej klasie. Importy IntelliJ podpowiada przez
**Alt+Enter**; trzeba sprawdzić, czy importowany komponent pochodzi z Swinga,
czy z JavaFX.

## Struktura i uruchamianie

```text
labN/
├── pom.xml
├── src/main/java/pl/uczelnia/labN/  ← kod Java
├── src/main/resources/            ← FXML, CSS, konfiguracja, SQL
├── src/test/java/                 ← testy
└── data/                          ← lokalna baza i pliki robocze
```

W przypadku istniejącego projektu należy otworzyć jego `pom.xml` przez
**Open as Project**, a nie sam katalog z klasami. W **Project Structure → Project SDK**
trzeba ustawić JDK 27, a **Language level** na 27 bez preview. W **Modules →
Dependencies** należy ustawić Module SDK zgodny z Project SDK. W **Build Tools →
Maven → Runner** i **Importing** trzeba wskazać JDK 27. Jeżeli IDE nie obsługuje
poziomu 27, należy zainstalować aktualne stabilne IntelliJ IDEA, nie wydanie EAP.
Wbudowany Maven wystarcza; polecenie `mvn` w zewnętrznym terminalu wymaga osobnej
instalacji lub wrappera.

W terminalu należy sprawdzić `java --version` oraz `javac --version`; obydwa
powinny wskazywać wersję 27 bez oznaczenia `-ea`. Przy zewnętrznym Maven trzeba
również sprawdzić `mvn --version`; Java używana przez Maven powinna być w wersji 27.
Ustawienia terminala i Runnera IntelliJ mogą się różnić, dlatego przy korzystaniu
z obu środowisk należy zweryfikować każde z nich.

| Laboratorium | Uruchomienie w IntelliJ | Oczekiwany efekt |
|---|---|---|
| Swing | strzałka przy `Main.main` | okno desktopowe |
| JavaFX | okno Maven → Plugins → javafx → `javafx:run` | okno desktopowe |
| JDBC | `Main.main` po synchronizacji Maven | komunikat i plik SQLite, następnie GUI |
| Hibernate | `Main.main` po synchronizacji Maven | SQL w konsoli i plik H2 |
| JSP | Maven → Lifecycle → `package`, następnie Tomcat | strona pod adresem HTTP |

W konfiguracji uruchomienia **Working directory** należy ustawić na katalog
projektu zawierający `pom.xml`. Wtedy `data/economy.db` (SQLite w Lab 3) oraz
`data/economy_orm.mv.db` (H2 w Lab 4) znajdują się w katalogu `data` właściwego
projektu. W adresie JDBC H2 należy podać `jdbc:h2:./data/economy_orm`, bez
rozszerzenia `.mv.db`. `src/main/resources` jest katalogiem zasobów odczytywanych przez classpath;
nie należy zapisywać w nim wyników podczas działania aplikacji.

## Debugger i sprawdzanie działania

W testach GUI stosuj [procedurę responsywności, HiDPI i motywów](jakosc-gui.html).
Motyw IntelliJ i skalowanie jego edytora nie są ustawieniami uruchamianej aplikacji.
Należy sprawdzić okno aplikacji, menu i dialogi przy systemowej skali 100%, 150% i 200%.
Gdy zmiana skali systemu jest niedostępna, należy wykonać
[pomocnicze próby z parametrami JVM](jakosc-gui.html#pomocnicze-wymuszenie-skali-w-windows)
i zaznaczyć w raporcie sposób uzyskania skali.

Breakpoint należy ustawić przy walidacji lub wywołaniu DAO, a następnie uruchomić
**Debug** i wywołać zdarzenie w GUI. W debuggerze trzeba sprawdzić argumenty,
kolekcje i stos wywołań. **Step Over** wykonuje bieżącą linię, a **Step Into**
wchodzi do metody. Zatrzymanie EDT lub JavaFX Application Thread zamraża okno;
jest to normalne podczas debugowania.

Przed oddaniem należy uruchomić **Maven → Lifecycle → clean**, a następnie
**verify**. Udane budowanie nie zastępuje sprawdzenia GUI i wyników obliczeń.
Pierwsze pobranie bibliotek wymaga Internetu. Działanie gotowej aplikacji offline
należy sprawdzić po przygotowaniu zależności i danych.

| Objaw | Co sprawdzić |
|---|---|
| `invalid target release: 27` | Maven używa niewłaściwego JDK; należy ustawić JDK 27 w Runnerze lub `JAVA_HOME` terminala |
| czerwone importy biblioteki | zapis POM, synchronizacja Maven, dostęp do repozytorium zależności |
| `JavaFX runtime components are missing` | należy uruchomić `javafx:run`, a nie klasę z błędną konfiguracją classpath |
| `ClassNotFoundException` dla klasy startowej | zgodność `package`, ścieżki pliku i `mainClass` |
| pusta baza po ponownym uruchomieniu | working directory i pełna ścieżka bazy |
| `Location is not set` przy FXML | zasób nie trafił do `src/main/resources` lub ścieżka jest błędna |
| ostrzeżenie o ograniczonym dostępie natywnym z `javafx.graphics` | w opcjach JVM aplikacji należy zachować `--enable-native-access=javafx.graphics`; projekty JavaFX mają tę opcję w konfiguracji `javafx-maven-plugin` |

Opcja `--enable-native-access=javafx.graphics` zezwala temu modułowi na dostęp
do kodu natywnego. Mechanizm JDK obejmuje JNI oraz ograniczone operacje FFM;
ostrzeżenie przy ładowaniu bibliotek JavaFX nie oznacza błędu kodu formularza.
Opcję należy przekazać procesowi aplikacji, nie jako argument metody `main`.
Nie rozwiązuje ona błędów brakujących bibliotek ani ostrzeżeń innych modułów.

## Dokumentacja narzędzi

Diagramy Mermaid wyświetlają się na stronie materiałów GitHub Pages. Podgląd
Markdown w IntelliJ zależy od dostępnej integracji Mermaid; jeśli pokazuje sam
kod, obejrzyj diagram na stronie materiałów. Ilustracje PNG działają niezależnie
od obsługi Mermaid.

- [Pierwsza aplikacja Java w IntelliJ IDEA](https://www.jetbrains.com/help/idea/creating-and-running-your-first-java-application.html)
- [Obsługa Maven w IntelliJ IDEA](https://www.jetbrains.com/help/idea/maven-support.html)
- [JavaFX: konfiguracja i uruchamianie](https://openjfx.io/openjfx-docs/)
- [Tomcat 10.1: dokumentacja](https://tomcat.apache.org/tomcat-10.1-doc/)
