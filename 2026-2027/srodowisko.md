---
layout: default
title: "Środowisko laboratoriów — IntelliJ IDEA"
---

# Środowisko laboratoriów — IntelliJ IDEA

Pracujemy w IntelliJ IDEA, w projektach Maven. Używamy najnowszych stabilnych
wydań JDK i JavaFX, podanych poniżej jako konkretne wersje dla tej edycji instrukcji.

Stan zweryfikowany **13 października 2026**:

| Element | Wersja w laboratoriach |
|---|---|
| JDK | **27 (GA)** |
| JavaFX | **27** — wszystkie moduły `org.openjfx` |
| Maven `maven.compiler.release` | **27** |
| IntelliJ Project SDK, Module SDK, Maven Runner i Importer | **JDK 27** |
| Language level | **27**, bez preview |

Nie instaluj wydań EA, alpha, beta, RC ani SNAPSHOT bibliotek. Nie włączaj
`--enable-preview` i nie używaj API inkubacyjnych. Oznaczenie `1.0-SNAPSHOT`
własnego projektu w przykładowym POM jest numerem roboczym aplikacji studenta,
nie wersją JDK ani zależnością prerelease.

JavaFX 27 wymaga co najmniej JDK 25; przyjęty tu JDK 27 spełnia ten warunek.
Numerów JDK i JavaFX nie dobiera się na podstawie samej równości numerów.
W tej instrukcji konfigurujemy dokładnie JDK 27 i JavaFX 27.
Po wydaniu kolejnych stabilnych wersji aktualizujemy tę tabelę i przykłady
razem, po sprawdzeniu zgodności — nie używamy dynamicznych wersji `LATEST`.

Źródła wersji: [wydania GA JDK 27](https://www.oracle.com/java/technologies/javase/27u-relnotes.html)
oraz [wydanie i wymagania JavaFX 27](https://gluonhq.com/products/javafx/openjfx-27-release-notes/).

## Pierwszy projekt

1. Wybierz **New Project → Java**, nazwij projekt `lab1`, wybierz **Maven** jako system budowania.
2. W polu JDK wskaż JDK 27 GA albo użyj **Download JDK → Version 27**, wybierając wydanie bez oznaczenia Early Access. Samo środowisko uruchomieniowe IDE nie zastępuje JDK projektu.
3. Ustaw GroupId `pl.uczelnia` i ArtifactId `lab1`. Dla kolejnych labów zmieniaj numer.
4. Otwórz `pom.xml` i zastosuj poniższą bazę. Zależności z instrukcji dodawaj wewnątrz `dependencies`, a pluginy wewnątrz `build/plugins`.
5. Po zmianie POM wybierz **Load Maven Changes / Reload All Maven Projects**. Nazwa przycisku zależy od wersji IDE.
6. W `src/main/java` utwórz pakiet `pl.uczelnia.lab1`, a w nim klasę `Main`. Pierwsza linia pliku to `package pl.uczelnia.lab1;`.
7. Uruchom `main` zieloną strzałką przy metodzie. Konsola **Run** pokazuje komunikaty i wyjątki.

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

Fragmenty XML w labach uzupełniają ten plik — nie wklejaj drugiego `project`, `build` ani `dependencies`. Przykłady z pojedynczym listenerem lub metodą są fragmentami do umieszczenia w istniejącej klasie. IntelliJ podpowiada importy przez **Alt+Enter**; sprawdzaj, czy importujesz komponent Swing, czy JavaFX.

## Struktura i uruchamianie

```text
labN/
├── pom.xml
├── src/main/java/pl/uczelnia/labN/  ← kod Java
├── src/main/resources/            ← FXML, CSS, konfiguracja, SQL
├── src/test/java/                 ← testy
└── data/                          ← lokalna baza i pliki robocze
```

Otwierając gotowy projekt, wybierz jego `pom.xml` i **Open as Project**. Nie otwieraj samego katalogu z klasami. W **Project Structure → Project SDK** ustaw JDK 27, a **Language level** na 27 bez preview. W **Modules → Dependencies** ustaw Module SDK na Project SDK. W ustawieniach **Build Tools → Maven → Runner** i **Importing** wskaż JDK 27. Jeżeli IDE nie obsługuje poziomu 27, zainstaluj aktualne stabilne IntelliJ IDEA, nie wydanie EAP. Wbudowany Maven wystarcza; polecenie `mvn` w zewnętrznym terminalu wymaga osobnej instalacji lub wrappera.

W terminalu sprawdź `java --version` oraz `javac --version`: obydwa mają wskazywać
27, bez oznaczenia `-ea`. Przy zewnętrznym Maven sprawdź również `mvn --version`:
Java używana przez Maven ma być w wersji 27. Ustawienia terminala i Runnera IntelliJ
mogą się różnić — zweryfikuj oba środowiska, jeśli korzystasz z obu.

| Laboratorium | Uruchomienie w IntelliJ | Oczekiwany efekt |
|---|---|---|
| Swing | strzałka przy `Main.main` | okno desktopowe |
| JavaFX | okno Maven → Plugins → javafx → `javafx:run` | okno desktopowe |
| JDBC | `Main.main` po synchronizacji Maven | komunikat i plik SQLite, następnie GUI |
| Hibernate | `Main.main` po synchronizacji Maven | SQL w konsoli i plik H2 |
| JSP | Maven → Lifecycle → `package`, następnie Tomcat | strona pod adresem HTTP |

W konfiguracji uruchomienia ustaw **Working directory** na katalog projektu z `pom.xml`. Wtedy `data/products.db` oznacza zawsze tę samą bazę. `src/main/resources` jest katalogiem zasobów odczytywanych przez classpath; nie jest katalogiem do zapisywania wyników podczas działania aplikacji.

## Debugger i sprawdzanie działania

W testach GUI stosuj [procedurę responsywności, HiDPI i motywów](jakosc-gui.html).
Motyw IntelliJ i skalowanie jego edytora nie są ustawieniami uruchamianej aplikacji.
Testuj okno aplikacji, menu i dialogi przy systemowej skali 100%, 150% i 200%.

Postaw breakpoint przy walidacji lub wywołaniu DAO, uruchom **Debug** i wywołaj zdarzenie w GUI. Sprawdź argumenty, kolekcje i stos wywołań. **Step Over** wykonuje bieżącą linię, a **Step Into** wchodzi do metody. Zatrzymanie EDT lub JavaFX Application Thread zamraża okno — to normalne podczas debugowania.

Przed oddaniem uruchom **Maven → Lifecycle → clean**, potem **verify**. Udane budowanie nie zastępuje sprawdzenia GUI i wyników obliczeń. Pierwsze pobranie bibliotek wymaga Internetu. Działanie gotowej aplikacji offline sprawdzamy po przygotowaniu zależności i danych.

| Objaw | Co sprawdzić |
|---|---|
| `invalid target release: 27` | Maven używa niewłaściwego JDK; ustaw JDK 27 w Runnerze lub `JAVA_HOME` terminala |
| czerwone importy biblioteki | zapis POM, synchronizacja Maven, dostęp do repozytorium zależności |
| `JavaFX runtime components are missing` | uruchom `javafx:run`; nie samą klasę z błędną konfiguracją classpath |
| `ClassNotFoundException` dla klasy startowej | zgodność `package`, ścieżki pliku i `mainClass` |
| pusta baza po ponownym uruchomieniu | working directory i pełna ścieżka bazy |
| `Location is not set` przy FXML | zasób nie trafił do `src/main/resources` lub ścieżka jest błędna |

## Dokumentacja narzędzi

Diagramy Mermaid wyświetlają się na stronie materiałów GitHub Pages. Podgląd
Markdown w IntelliJ zależy od dostępnej integracji Mermaid; jeśli pokazuje sam
kod, obejrzyj diagram na stronie materiałów. Ilustracje PNG działają niezależnie
od obsługi Mermaid.

- [Pierwsza aplikacja Java w IntelliJ IDEA](https://www.jetbrains.com/help/idea/creating-and-running-your-first-java-application.html)
- [Obsługa Maven w IntelliJ IDEA](https://www.jetbrains.com/help/idea/maven-support.html)
- [JavaFX: konfiguracja i uruchamianie](https://openjfx.io/openjfx-docs/)
- [Tomcat 10.1: dokumentacja](https://tomcat.apache.org/tomcat-10.1-doc/)
