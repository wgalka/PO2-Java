---
layout: default
title: "JavaDoc i dystrybucja aplikacji Java"
---

# JavaDoc i dystrybucja aplikacji Java

JavaDoc tworzy dokumentację HTML na podstawie komentarzy umieszczonych w kodzie
źródłowym Javy. W projekcie jest to dokumentacja API: opisuje klasy i metody,
z których korzystają inne części aplikacji lub jej przyszli opiekunowie.

Instrukcja dotyczy projektów tworzonych w IntelliJ IDEA z użyciem Mavena i JDK
**27 GA**. Ustawienia środowiska opisano w
[instrukcji IntelliJ IDEA i Maven](srodowisko.html).

## 1. Komentarz dokumentacyjny

Komentarz JavaDoc rozpoczyna się od `/**` i znajduje bezpośrednio nad
deklaracją, którą opisuje. Zwykły komentarz `//` ani blok `/* ... */` do
generowania dokumentacji nie jest używany.

```java
/**
 * Repozytorium odczytujące obserwacje wskaźników ekonomicznych.
 */
public final class ObservationRepository {
    // ...
}
```

Pierwsze zdanie powinno zwięźle wyjaśniać przeznaczenie klasy lub metody. Jest
wykorzystywane między innymi w tabelach podsumowujących wygenerowanej strony.
Komentarz opisuje zachowanie widoczne dla użytkownika API, a nie powtarza
kolejnych instrukcji z implementacji.

W metodzie należy opisać warunki wejściowe, wynik, kolejność lub jednostki
wartości oraz sytuacje wyjątkowe, jeśli wpływają na sposób użycia. Jest to
szczególnie ważne dla danych ekonometrycznych: sama nazwa `mean` nie wyjaśnia,
jak traktowane są braki danych, jakiego okresu dotyczy wynik ani w jakiej
jednostce jest zwracany.

```java
/**
 * Zwraca obserwacje wskaźnika w podanym zakresie miesięcy.
 *
 * <p>Oba końce zakresu są włączone, a wynik jest uporządkowany chronologicznie.
 *
 * @param indicatorCode niepusty kod wskaźnika, na przykład {@code CPI}
 * @param from pierwszy miesiąc zakresu
 * @param to ostatni miesiąc zakresu
 * @return obserwacje z zakresu albo pustą listę, jeżeli ich nie znaleziono
 * @throws IllegalArgumentException gdy kod jest pusty albo {@code from} jest
 *                                  późniejsze niż {@code to}
 * @see Observation
 */
List<Observation> findObservations(
        String indicatorCode,
        YearMonth from,
        YearMonth to
);
```

## 2. Tagi i odwołania

Tagi umieszcza się po opisie, każdy w osobnym wierszu. Należy używać tylko tych,
które odpowiadają rzeczywistemu kontraktowi deklaracji.

| Tag | Zastosowanie |
|---|---|
| `@param nazwa opis` | Opisuje każdy parametr metody w kolejności z deklaracji. |
| `@return opis` | Wyjaśnia wynik metody zwracającej wartość; nie stosuje się go do `void`. |
| `@throws Typ opis` | Podaje, w jakim warunku metoda zgłasza dany wyjątek. |
| `@see Typ` | Dodaje odwołanie do powiązanej klasy lub metody. |
| `@since wersja` | Wskazuje wersję, w której element API został dodany. |
| `@deprecated opis` | Wyjaśnia, dlaczego element nie powinien być dalej używany i czego użyć zamiast niego. |

Wewnątrz opisu można używać między innymi:

- `{@code tekst}` do pokazania nazwy klasy, parametru, wartości albo krótkiego fragmentu kodu;
- `{@link Observation}` lub `{@link ObservationRepository#findObservations(String, YearMonth, YearMonth)}` do utworzenia klikalnego odwołania do API.

Jeżeli element jest przestarzały, tag `@deprecated` należy stosować razem
z adnotacją `@Deprecated` w kodzie. Nie należy dodawać `@author` i `@version`
do każdej klasy ani wpisywać tagów, które nie niosą konkretnej informacji.

## 3. Komentarze w IntelliJ IDEA

W edytorze można wpisać `/**` bezpośrednio przed deklaracją i nacisnąć `Enter`.
IntelliJ IDEA wstawi szablon komentarza i tagi odpowiadające parametrom,
wartości zwracanej oraz deklarowanym wyjątkom. Uzupełniony szablon trzeba
zredagować: automatycznie wstawione tagi nie wyjaśniają kontraktu metody.

JDK 27 obsługuje także komentarze dokumentacyjne w składni Markdown rozpoczynane
od `///`. W tej instrukcji przykłady stosują tradycyjny zapis `/** ... */`.
Jeżeli IntelliJ IDEA wstawia `///`, można zmienić preferencję w
**Settings → Editor → Code Style → Java → Code Generation → Comment Code**
i wyłączyć **Prefer documentation line comments**.

## 4. Generowanie dokumentacji HTML w IntelliJ IDEA

1. Otwórz projekt Maven w IntelliJ IDEA i zsynchronizuj jego zależności. Ustaw
   **Project SDK** i **Maven Runner** na JDK 27 GA.
2. Uzupełnij komentarze dokumentacyjne w klasach i metodach aplikacji. Wybierz
   kod źródłowy w `src/main/java`; nie włączaj `src/test/java`.
3. W menu wybierz **Tools → Generate Javadoc**.
4. W polu **Scope** wybierz pakiety aplikacji, które mają znaleźć się w
   dokumentacji. Nie wybieraj źródeł bibliotek JavaFX ani zależności Mavena.
5. Jako **Output directory** podaj `target/apidocs` w katalogu projektu.
6. Ustaw poziom widoczności **Public**. Jeśli projekt świadomie udostępnia
   podklasom elementy `protected`, wybierz **Protected**.
7. W polu argumentów wiersza poleceń wpisz:

   ```text
   -encoding UTF-8 -docencoding UTF-8 -charset UTF-8
   ```

   `-encoding` określa kodowanie plików źródłowych, a `-docencoding` i
   `-charset` ustawiają kodowanie generowanych stron HTML. Pole **Locale** można
   pozostawić puste.
8. Kliknij **Generate** i przeczytaj wynik w oknie **Run**. Popraw błędy
   składniowe i odwołania do nieistniejących elementów, a następnie wygeneruj
   dokumentację ponownie.
9. Otwórz `target/apidocs/index.html` w przeglądarce. Sprawdź stronę indeksu,
   opisy klas i metod, polskie znaki oraz odnośniki między elementami API.

Zakres widoczności decyduje o tym, które deklaracje zostaną pokazane.
**Public** obejmuje elementy publiczne; **Protected** obejmuje elementy publiczne
i chronione. Package-private i private nie są wtedy wyświetlane. Wewnętrzne
szczegóły implementacji nie powinny być publikowane jako kontrakt API.

## 5. Sprawdzenie i typowe poprawki

- Ostrzeżenie o brakującym opisie oznacza, że widoczna klasa lub metoda nie ma
  komentarza albo nie opisuje wszystkich parametrów, wyniku lub wyjątków.
- Błąd nierozpoznanego typu zwykle oznacza, że projekt Maven nie został
  zsynchronizowany albo generowanie uruchomiono z niewłaściwym JDK.
- Brak klasy w wyniku może wynikać z wybranego zakresu lub poziomu widoczności.
- Niepoprawne polskie znaki wskazują zwykle na brak opcji kodowania UTF-8.
- Nie wpisuj opisów ręcznie do wygenerowanego HTML. Popraw komentarze w plikach
  `.java` i wygeneruj dokumentację ponownie.

Katalog `target` zawiera wyniki budowania i nie należy go dodawać do repozytorium
kodu. Źródłem dokumentacji pozostają komentarze w plikach Java.

## 6. Dokumentacja kodu jako część projektu

Dokumentację API należy zapisywać w kodzie źródłowym, bezpośrednio nad
deklaracjami klas i metod. Plik README ani opis ekranów nie zastępują komentarzy
JavaDoc. W projekcie należy udokumentować publiczne i chronione elementy API,
które stanowią część modelu, repozytoriów, serwisów i analizy. Prywatne metody
pomocnicze wymagają komentarza tylko wtedy, gdy ich nazwa i implementacja nie
wyjaśniają istotnej reguły.

Opis powinien wyjaśniać kontrakt użycia: sens parametrów, ograniczenia zakresu,
jednostki, sposób traktowania braków, wynik oraz wyjątki. Nie należy opisywać
kodu linia po linii ani powtarzać samej nazwy metody. Wygenerowany HTML trzeba
sprawdzić i dołączyć do materiałów projektu w `docs/javadoc/`; źródłem opisów
pozostają komentarze w plikach `.java`.

## 7. Formaty dystrybucji aplikacji

| Format | Co otrzymuje użytkownik | Typowe zastosowanie |
|---|---|---|
| JAR | Archiwum klas i zasobów; runtime lub zależności mogą być wymagane osobno. | Uruchamianie przez `java -jar` albo jako zależność innego programu. |
| Obraz `app-image` | Katalog z launcherem `.exe`, aplikacją i własnym runtime’em. | Przenośny pakiet do rozpakowania i uruchomienia; można go spakować do ZIP. |
| Instalator `.exe` | Instalator Windows, który instaluje aplikację i runtime oraz może utworzyć skróty. | Wygodna dystrybucja aplikacji dla użytkowników Windows. |
| Obraz `jlink` | Własny, platformowy runtime z modułami aplikacji i launchera, jeśli go skonfigurowano. | Budulec lub przenośny katalog uruchomieniowy; sam w sobie nie jest instalatorem. |

Zwykły JAR utworzony przez `mvn package` nie musi zawierać zależności. Przed
pakowaniem należy przygotować wykonywalny JAR wraz z zależnościami na classpath
albo projekt modularny i komplet modułów. `jpackage` nie pobiera zależności
z pliku `pom.xml`.

## 8. Budowanie pakietu dla Windows

Pakiet Windows należy budować w Windows, używając JDK **27 GA**. `jpackage`
dołącza środowisko Java do obrazu aplikacji, więc komputer odbiorcy nie wymaga
osobnej instalacji JDK. Formatów natywnych nie buduje się między systemami:
pakiet Windows powstaje w Windows.

### 8.1. Przygotowanie wykonywalnego JAR-a

1. W IntelliJ IDEA należy otworzyć projekt Maven i ustawić Project SDK oraz
   Maven Runner na JDK 27 GA.
2. Należy sprawdzić w `pom.xml` nazwę `artifactId`, wersję projektu i klasę
   zawierającą `main`.
3. Zwykły JAR Maven nie musi zawierać bibliotek zewnętrznych. Do przykładowego
   pakowania należy skonfigurować Maven Shade Plugin **3.6.2 GA**, który scala
   kod aplikacji i zależności w wykonywalny JAR. Wstawienie tego fragmentu do
   `<build><plugins>` w `pom.xml` ustawia klasę startową; jej nazwę należy
   dopasować do własnego projektu. Dla JavaFX należy wskazać klasę `Launcher`
   opisaną w sekcji 9.

   ```xml
   <plugin>
       <groupId>org.apache.maven.plugins</groupId>
       <artifactId>maven-shade-plugin</artifactId>
       <version>3.6.2</version>
       <executions>
           <execution>
               <phase>package</phase>
               <goals><goal>shade</goal></goals>
               <configuration>
                   <transformers>
                       <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                           <mainClass>pl.uczelnia.projekt.Main</mainClass>
                       </transformer>
                       <transformer implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer" />
                   </transformers>
               </configuration>
           </execution>
       </executions>
   </plugin>
   ```

4. Należy uruchomić `mvn clean package`. W przykładzie POM ma
   `<artifactId>analiza</artifactId>` i `<version>1.0.0</version>`, dlatego
   wynik to `target/analiza-1.0.0.jar`. Jeżeli nazwy w POM są inne, należy użyć
   rzeczywistej nazwy pliku z `target/`.
5. W terminalu PowerShell otwartym w katalogu zawierającym `pom.xml` należy
   przygotować katalog wejściowy `jpackage`:

   ```powershell
   New-Item -ItemType Directory -Force target/package-input | Out-Null
   Copy-Item target/analiza-1.0.0.jar target/package-input/analiza-1.0.0.jar
   ```

   W `target/package-input/` znajduje się jeden wykonywalny JAR zawierający
   zależności. Należy sprawdzić jego uruchomienie poleceniem
   `java -jar target/package-input/analiza-1.0.0.jar` przed pakowaniem.

JavaFX bez modułów wymaga dodatkowo punktu wejścia, który nie dziedziczy po
`Application`; oficjalny przykład OpenJFX opisuje tę regułę. Zależności JavaFX
oraz ich biblioteki natywne są platformowe, dlatego JAR do dystrybucji Windows
należy budować w Windows.

### 8.2. Obraz aplikacji `app-image`

Poniższy przykład zakłada plik `analiza-1.0.0.jar` przygotowany w poprzednim
punkcie. Dla aplikacji Swing albo aplikacji bez JavaFX klasa startowa to
`pl.uczelnia.projekt.Main`. W aplikacji JavaFX należy użyć klasy `Launcher`
opisanej w sekcji 9.

W terminalu PowerShell otwartym w katalogu zawierającym `pom.xml` należy wykonać:

```powershell
jpackage --type app-image --name AnalizaWskaznikow --app-version 1.0.0 `
  --input target/package-input --main-jar analiza-1.0.0.jar `
  --main-class pl.uczelnia.projekt.Main --dest target/dist
```

Wynikiem jest katalog `target/dist/AnalizaWskaznikow/` zawierający
`AnalizaWskaznikow.exe`, pliki aplikacji i runtime. Należy uruchomić EXE
z tego katalogu, a następnie spakować cały katalog do ZIP-a, jeżeli pakiet ma
być przekazany jako wersja przenośna. Nie wolno kopiować samego pliku EXE:
launcher korzysta z pozostałej zawartości obrazu.

### 8.3. Instalator Windows `.exe`

Do instalatora `.exe` wymagany jest JDK 27 GA oraz WiX Toolset. Dokumentacja
JDK 27 wymaga WiX 3.0 lub nowszego; dla tej edycji należy używać stabilnego
WiX Toolset **7.0.0 GA**. Instalator narzędzi należy skonfigurować tak, aby
polecenie `wix` było dostępne w `PATH`. Instalator WiX nie wchodzi do pakietu
aplikacji i nie jest potrzebny na komputerze odbiorcy.

Po przygotowaniu `target/package-input/analiza-1.0.0.jar` należy wykonać:

```powershell
jpackage --type exe --name AnalizaWskaznikow --app-version 1.0.0 `
  --input target/package-input --main-jar analiza-1.0.0.jar `
  --main-class pl.uczelnia.projekt.Main --dest target/dist `
  --win-menu --win-shortcut --win-per-user-install
```

W `target/dist/` powstanie instalator `AnalizaWskaznikow-1.0.0.exe`.
Opcja `--win-menu` dodaje skrót w menu Start, `--win-shortcut` dodaje skrót
na pulpicie, a `--win-per-user-install` ogranicza instalację do profilu
użytkownika. Własną ikonę `.ico` można dodać przez `--icon ścieżka\ikona.ico`.

### 8.4. Sprawdzenie pakietu

Pakiet należy sprawdzić na czystym koncie Windows lub komputerze bez JDK,
bez Internetu i bez katalogu roboczego projektu. Należy sprawdzić:

1. czy aplikacja uruchamia się z menu Start lub z obrazu `app-image`;
2. czy działają import danych, baza, wykresy, eksport i lokalne zasoby;
3. czy ustawienia i baza są zapisywane w katalogu użytkownika, a nie wewnątrz
   `Program Files` ani katalogu instalacji;
4. czy po ponownym uruchomieniu pozostają dane i preferencje użytkownika;
5. czy aplikacja zachowuje wymagane motywy i czytelność przy skalach 100%, 150%
   i 200%.

Pliki bazy, które zmienia użytkownik, nie powinny być zapisywane wewnątrz
instalatora ani zasobów JAR. Katalog instalacji może być tylko do odczytu;
dane aplikacji należy przechowywać w katalogu danych użytkownika. Do repozytorium
nie należy dodawać `target/`; do oddania należy skopiować wygenerowany HTML do
`docs/javadoc/`, a obraz aplikacji przekazać jako osobne archiwum dystrybucyjne.

## 9. Projekty JavaFX

JavaFX wymaga dodatkowego przygotowania zależności i plików natywnych. W bieżącym
projekcie startowym Lab 2 nie ma `module-info.java` i instrukcja Lab 2 zabrania
jego tworzenia w tym ćwiczeniu. Nie należy uruchamiać w nim `javafx:jlink` jako
gotowego przepisu: cel `jlink` wtyczki JavaFX wymaga projektu modularnego.

W projekcie semestralnym można wybrać jedną z dwóch ścieżek:

1. **Projekt modularny:** dodać `module-info.java` z używanymi modułami,
   eksportami pakietów oraz `opens ... to javafx.fxml` dla kontrolerów FXML.
   W konfiguracji `javafx-maven-plugin` należy podać nazwę modułu i klasy
   głównej oraz nazwę launchera, np.:

   ```xml
   <configuration>
       <mainClass>pl.uczelnia.projekt/pl.uczelnia.projekt.Main</mainClass>
       <launcher>AnalizaWskaznikow</launcher>
       <jlinkImageName>image</jlinkImageName>
       <bindServices>true</bindServices>
   </configuration>
   ```

   Następnie należy utworzyć platformowy obraz runtime poleceniem
   `mvn clean javafx:jlink`. Obraz zawiera własny runtime. Ponieważ w przykładzie
   skonfigurowano launcher, na Windows jego skrypt uruchamiający znajduje się
   w `target/image/bin/AnalizaWskaznikow.bat`. Ten obraz jest przenośnym pakietem,
   ale sam nie jest instalatorem `.exe`. Opcja `bindServices` dodaje do obrazu
   wiązania usług wymagane przez część bibliotek.
2. **Projekt niemodularny:** utworzyć wykonywalny JAR zawierający potrzebne
   zależności JavaFX oraz natywne biblioteki dla Windows. Dla takiego JAR-a
   należy dodać klasę startową, która nie dziedziczy po `Application`, i wskazać
   ją jako main class w `pom.xml` i w poleceniu `jpackage`. Następnie można
   użyć procedury `jpackage` z poprzedniego punktu. Szczegóły konfiguracji
   Maven Shade i klasy startowej opisuje
   [dokumentacja OpenJFX dla projektu niemodularnego](https://openjfx.io/openjfx-docs/modular.html#Non-modular_application).

Przykładowa klasa startowa dla projektu, w którym `Main` dziedziczy po
`Application`, może wyglądać następująco:

```java
package pl.uczelnia.projekt;

import javafx.application.Application;

public final class Launcher {
    private Launcher() {
    }

    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
```

W konfiguracji Maven Shade należy wtedy ustawić `<mainClass>` na
`pl.uczelnia.projekt.Launcher`, a w poleceniu `jpackage` zmienić argument
`--main-class` na tę samą nazwę. W projekcie modularnym zależność, która nie
ma deskryptora modułu ani `Automatic-Module-Name`, może uniemożliwić użycie
`jlink`; należy wtedy użyć wariantu niemodularnego albo zastąpić tę zależność
biblioteką zgodną z module path.

### 9.1. Hibernate i problemy z `jlink`

Połączenie Hibernate i JavaFX nie oznacza samo w sobie, że `jlink` jest
niemożliwy, ale projekt modularny wymaga sprawdzenia całego grafu modułów.
Wtyczka JavaFX wymaga, aby zależności umieszczone na module path były
modularnymi bibliotekami albo miały wpis `Automatic-Module-Name`. Hibernate
korzysta także z mechanizmu `ServiceLoader` do odnajdywania dostawców i
integracji. Brak modułu lub dostawcy w obrazie może więc ujawnić się dopiero
podczas uruchomienia albo inicjalizacji bazy danych.

Przy diagnozowaniu projektu modularnego należy sprawdzić kolejno:

1. Czy każda zależność z module path ma deskryptor modułu lub
   `Automatic-Module-Name`. Jeżeli nie, należy przenieść ją na classpath,
   znaleźć zgodną wersję biblioteki albo zrezygnować z modularnego obrazu.
2. Czy pakiety encji i kontrolerów otwierane przez refleksję mają właściwe
   deklaracje `opens` w `module-info.java`.
3. Czy obraz zawiera używane implementacje usług i dostawcę JDBC. Dla
   `javafx-maven-plugin` należy włączyć `<bindServices>true</bindServices>`;
   domyślnie wtyczka nie przekazuje opcji `--bind-services` do `jlink`.
4. Czy aplikacja w gotowym obrazie wykonuje operację Hibernate rzeczywistą dla
   projektu: inicjalizuje schemat, zapisuje rekord, odczytuje go i zamyka
   połączenie.

W JDK 25 i nowszych `jpackage` również nie dołącza wiązań usług do tworzonego
   runtime’u domyślnie. Przy modularnym pakowaniu przez `jpackage` należy
   przekazać je jawnie wraz z domyślnymi opcjami `jlink`, na przykład:

```powershell
jpackage ... `
  --jlink-options "--strip-native-commands --strip-debug --no-man-pages --no-header-files --bind-services"
```

Ta opcja pomaga tylko wtedy, gdy brakowały wiązania usług. Nie naprawia
niezgodnych modułów, niedostępnych zasobów, brakujących bibliotek natywnych ani
błędnych deklaracji `opens`. Bez komunikatu błędu nie należy zakładać, że to
była przyczyna wcześniejszego problemu.

Dla projektu, którego zależności Hibernate lub JavaFX sprawiają kłopot na
module path, należy wybrać ścieżkę niemodularną z sekcji 8: wykonywalny JAR na
classpath, `Launcher`, zachowanie plików `META-INF/services` przez
`ServicesResourceTransformer`, a następnie `jpackage --type app-image` lub
instalator `.exe`. Nadal trzeba sprawdzić gotowy pakiet na czystym Windowsie,
w tym połączenie z bazą i operacje zapisu oraz odczytu. `jpackage` tworzy własny
runtime, ale nie wymaga wtedy umieszczania wszystkich bibliotek aplikacji na
module path.

`ServicesResourceTransformer` i `--bind-services` rozwiązują różne problemy.
Pierwszy scala wpisy `META-INF/services` z bibliotek dołączanych do
wykonywalnego JAR-a na classpath. Drugi wiąże usługi dostępne jako moduły przy
tworzeniu obrazu runtime przez `jlink`. Włączenie jednego mechanizmu nie zastępuje
drugiego.

Nie należy scalać ręcznie wszystkich plików JavaFX do JAR-a ani kopiować DLL-i
z pamięci podręcznej Mavena. Taki pakiet łatwo staje się zależny od wersji
systemu i architektury. Zależności JavaFX muszą odpowiadać wersji **JavaFX 27**
i platformie docelowej.

## 10. Starsze sposoby pakowania

W starszym materiale do przedmiotu opisano tworzenie JAR-a przez IntelliJ IDEA:
**File → Project Structure → Artifacts → + → JAR → From modules with
dependencies**, wybór modułu i klasy głównej, a następnie
**Build → Build Artifacts**. IntelliJ zapisuje wynik pod `out/artifacts/`.
Tę procedurę należy znać jako starszą metodę pracy. W projekcie Maven należy
preferować `mvn clean package`, ponieważ konfiguracja i wynik budowania są
zapisane w `pom.xml` i łatwiej je powtórzyć na innym komputerze.

**Launch4j** jest historycznym narzędziem do utworzenia windowsowego launchera
`.exe` dla JAR-a. Sam launcher nie oznacza, że aplikacja zawiera runtime.
Trzeba było skonfigurować wymagane JRE albo dostarczyć jego katalog razem
z aplikacją. Narzędzie jest przydatne do rozpoznania starszego podejścia, ale
nie należy go wybierać jako domyślnego procesu nowego projektu.

`jlink` nie tworzy instalatora ani samodzielnego pliku `.exe`: łączy potrzebne
moduły w własny, platformowy runtime. `jpackage` buduje obraz aplikacji lub
instalator i w razie potrzeby korzysta z `jlink`. Plik `.war` opisany w dawnym
materiale służy do wdrażania aplikacji serwerowych w kontenerze servletów;
nie jest formatem dystrybucji aplikacji desktopowej.

Starszy materiał: [JAR, Launch4j i jlink — instrukcja w repozytorium Java](https://github.com/wgalka/Java/blob/master/instrukcje/jar.md).

## Dokumentacja

- [JavaDoc Guide dla JDK 27](https://docs.oracle.com/en/java/javase/27/javadoc/index.html)
- [Składnia komentarzy dokumentacyjnych JDK 27](https://docs.oracle.com/en/java/javase/27/docs/specs/javadoc/doc-comment-spec.html)
- [Polecenie `javadoc` w JDK 27](https://docs.oracle.com/en/java/javase/27/docs/specs/man/javadoc.html)
- [JavaDoc w IntelliJ IDEA](https://www.jetbrains.com/help/idea/javadocs.html)
- [Starsza instrukcja JAR, Launch4j i jlink](https://github.com/wgalka/Java/blob/master/instrukcje/jar.md)
- [Polecenie `jpackage` w JDK 27](https://docs.oracle.com/en/java/javase/27/docs/specs/man/jpackage.html)
- [Przygotowanie i wymagania `jpackage` w JDK 27](https://docs.oracle.com/en/java/javase/27/jpackage/packaging-overview.html)
- [JavaFX Maven Plugin — `javafx:jlink`](https://github.com/openjfx/javafx-maven-plugin)
- [Hibernate ORM — integracje i odnajdywanie usług](https://docs.hibernate.org/orm/7.1/integrationguide/html_single/)
- [Apache Maven Shade Plugin 3.6.2](https://maven.apache.org/plugins/maven-shade-plugin/)
- [JavaFX: pakowanie projektu niemodularnego](https://openjfx.io/openjfx-docs/modular.html#Non-modular_application)
- [WiX Toolset 7.0.0 GA](https://github.com/wixtoolset/wix/releases/tag/v7.0.0)
