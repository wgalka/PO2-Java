# Środowisko laboratoriów — IntelliJ IDEA

Pracujemy w IntelliJ IDEA, w projektach Maven. Przykłady używają składni Javy 17.
Wybierz JDK 17; jeżeli pracownia ma nowszy JDK, pozostaw `release` równy 17.
Nie używamy funkcji preview. Numer JDK i wersje bibliotek zapisz w README projektu.

## Pierwszy projekt

1. Wybierz **New Project → Java**, nazwij projekt `lab1`, wybierz **Maven** jako system budowania.
2. W polu JDK wskaż zainstalowany JDK albo użyj **Download JDK**. Samo środowisko uruchomieniowe IDE nie zastępuje JDK projektu.
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
        <maven.compiler.release>17</maven.compiler.release>
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

Otwierając gotowy projekt, wybierz jego `pom.xml` i **Open as Project**. Nie otwieraj samego katalogu z klasami. W **Project Structure → Project SDK** oraz ustawieniach **Build Tools → Maven → Runner** wybierz zgodny JDK. Wbudowany Maven w IntelliJ wystarcza; polecenie `mvn` w zewnętrznym terminalu wymaga osobnej instalacji lub wrappera.

| Laboratorium | Uruchomienie w IntelliJ | Oczekiwany efekt |
|---|---|---|
| Swing | strzałka przy `Main.main` | okno desktopowe |
| JavaFX | okno Maven → Plugins → javafx → `javafx:run` | okno desktopowe |
| JDBC | `Main.main` po synchronizacji Maven | komunikat i plik SQLite, następnie GUI |
| Hibernate | `Main.main` po synchronizacji Maven | SQL w konsoli i plik H2 |
| JSP | Maven → Lifecycle → `package`, następnie Tomcat | strona pod adresem HTTP |

W konfiguracji uruchomienia ustaw **Working directory** na katalog projektu z `pom.xml`. Wtedy `data/products.db` oznacza zawsze tę samą bazę. `src/main/resources` jest katalogiem zasobów odczytywanych przez classpath; nie jest katalogiem do zapisywania wyników podczas działania aplikacji.

## Debugger i sprawdzanie działania

W testach GUI stosuj [procedurę responsywności, HiDPI i motywów](jakosc-gui.md).
Motyw IntelliJ i skalowanie jego edytora nie są ustawieniami uruchamianej aplikacji.
Testuj okno aplikacji, menu i dialogi przy systemowej skali 100%, 150% i 200%.

Postaw breakpoint przy walidacji lub wywołaniu DAO, uruchom **Debug** i wywołaj zdarzenie w GUI. Sprawdź argumenty, kolekcje i stos wywołań. **Step Over** wykonuje bieżącą linię, a **Step Into** wchodzi do metody. Zatrzymanie EDT lub JavaFX Application Thread zamraża okno — to normalne podczas debugowania.

Przed oddaniem uruchom **Maven → Lifecycle → clean**, potem **verify**. Udane budowanie nie zastępuje sprawdzenia GUI i wyników obliczeń. Pierwsze pobranie bibliotek wymaga Internetu. Działanie gotowej aplikacji offline sprawdzamy po przygotowaniu zależności i danych.

| Objaw | Co sprawdzić |
|---|---|
| `invalid target release: 17` | JDK używany przez Maven jest starszy niż 17 |
| czerwone importy biblioteki | zapis POM, synchronizacja Maven, dostęp do repozytorium zależności |
| `JavaFX runtime components are missing` | uruchom `javafx:run`; nie samą klasę z błędną konfiguracją classpath |
| `ClassNotFoundException` dla klasy startowej | zgodność `package`, ścieżki pliku i `mainClass` |
| pusta baza po ponownym uruchomieniu | working directory i pełna ścieżka bazy |
| `Location is not set` przy FXML | zasób nie trafił do `src/main/resources` lub ścieżka jest błędna |

## Dokumentacja narzędzi

Diagramy w instrukcjach są zapisane w blokach `mermaid` i można je wyświetlać
w podglądzie Markdown na GitHubie. Obsługa w podglądzie IntelliJ zależy od dostępnej
integracji Mermaid; jeśli widzisz sam kod diagramu, otwórz dokument na GitHubie.
Ilustracje PNG działają niezależnie od obsługi Mermaid.

- [Pierwsza aplikacja Java w IntelliJ IDEA](https://www.jetbrains.com/help/idea/creating-and-running-your-first-java-application.html)
- [Obsługa Maven w IntelliJ IDEA](https://www.jetbrains.com/help/idea/maven-support.html)
- [JavaFX: konfiguracja i uruchamianie](https://openjfx.io/openjfx-docs/)
- [Tomcat 10.1: dokumentacja](https://tomcat.apache.org/tomcat-10.1-doc/)
