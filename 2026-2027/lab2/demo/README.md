---
layout: default
title: "Layout Explorer — działająca aplikacja JavaFX"
---

# Layout Explorer — działająca aplikacja JavaFX

Gotową aplikację można [pobrać jako projekt Maven](layout-explorer.zip),
rozpakować i otworzyć w IntelliJ IDEA jako folder zawierający `pom.xml`.
Parametry kontenerów, rozmiar próbki i motyw można zmieniać w działającym GUI.
Kompletny pulpit analizy danych do dalszej modyfikacji znajduje się w
[projekcie startowym JavaFX](../projekt-startowy.zip).

Plik [pom.xml](pom.xml) należy otworzyć jako projekt Maven w IntelliJ IDEA.
Następnie trzeba ustawić **JDK 27 GA** jako Project SDK i Maven Runner,
zsynchronizować zależności i uruchomić
**Maven → Plugins → javafx → javafx:run**. Projekt używa **JavaFX 27**,
bez FXML i bez `module-info.java`. Cała logika demonstracji znajduje się
w [LayoutExplorer.java](src/main/java/pl/uczelnia/lab2/LayoutExplorer.java).

## Obsługa Layout Explorer

1. Wybrać przykład z listy: `HBox`, `VBox`, `FlowPane`, `TilePane`, `BorderPane`,
   `GridPane`, `StackPane`, `AnchorPane`, `ScrollPane / Accordion` lub `Zdarzenia`.
2. Zmienić suwakiem szerokość próbki. Jest to rozmiar kontenera w jednostkach
   logicznych, a nie zmiana DPI monitora; można również zmieniać rozmiar okna aplikacji.
3. Porównać `Spacing / gap` z `Padding`. Odstępy między dziećmi zmieniają się
   w HBox, VBox, FlowPane, TilePane i GridPane, a padding — we wszystkich próbkach.
4. Zmienić wyrównanie w HBox, VBox, FlowPane, TilePane i GridPane. Przełącznik
   wzrostu ogranicza lub zwalnia maksymalny rozmiar pola w HBox/GridPane albo
   wysokość TextArea w VBox; priorytet rodzica pozostaje `ALWAYS`.
5. Włączyć ciemny motyw i większy tekst. Zmiana motywu nie tworzy kontrolek od
   nowa, dlatego wpisane dane pozostają. Motyw jest zapamiętywany między uruchomieniami.
6. W StackPane przełączyć nakładkę, w Accordion rozwinąć drugą sekcję,
   a w przykładzie Zdarzenia porównać dziennik z `consume()` i bez niego.

Zmiana rodzaju przykładu tworzy nową próbkę i zeruje jej dane — służy porównywaniu
niezależnych układów. Zmiana szerokości, motywu i czcionki zachowuje bieżącą próbkę.
Nie wszystkie regulatory mają sens dla każdego kontenera: BorderPane nie ma
własności `spacing`, a AnchorPane pozycjonuje według kotwic.

Próbka ma demonstracyjną wysokość 300 jednostek. Jeżeli przy ekstremalnych
odstępach zabraknie miejsca, należy obserwować ograniczenia kontenera i użyć
przewijania obszaru pokazu. W docelowym formularzu trzeba ustalić minimalny
użyteczny rozmiar i adaptacyjny układ zgodnie z [wymaganiami GUI](../../jakosc-gui.html).

## Eksperymenty

| Widok | Zmiana | Co porównać |
|---|---|---|
| HBox / VBox | wzrost włączony/wyłączony | dodatkowe miejsce i maksymalny rozmiar dziecka |
| FlowPane / TilePane | szerokość 900 → 360 | zawijanie i rozmiar komórek |
| BorderPane | szerokość 900 → 360 | stałe role boków i rozciągany środek |
| GridPane | szerokość i wzrost pól | wolne miejsce w kolumnie a rozmiar pola |
| StackPane | pokaż/ukryj nakładkę | kolejność warstw i możliwość kliknięcia pola |
| AnchorPane | szerokość 900 → 360 | dwie kotwice pola i narożnik przycisku |
| ScrollPane / Accordion | przewijanie i zmiana sekcji | viewport i najwyżej jedna rozwinięta sekcja |
| Zdarzenia | zatrzymanie w filtrze | kolejność komunikatów w dzienniku |

Do kodu należy dodać własny widok, np. formularz wyboru zmiennych z przyciskiem
analizy. Rezultat trzeba przewidzieć przed uruchomieniem. Pełne wyjaśnienia
i diagramy znajdują się w [Lab 2](../README.html).

## Kontrola techniczna

W konfiguracji Maven w IntelliJ należy uruchomić:

```text
clean compile javafx:run -Djavafx.args=--verify
```

Tryb `--verify` tworzy sceny bez pokazywania okna, stosuje CSS, przelicza rozmiary
dla obu motywów i dwóch szerokości oraz sprawdza zawijanie FlowPane.
Wymaga środowiska obsługującego JavaFX graphics; nie jest backendem headless.
Nie zastępuje ręcznych testów klawiatury, ostrości na monitorze ani macierzy HiDPI.
Bez `--verify` aplikacja uruchamia okno do interaktywnej pracy.
