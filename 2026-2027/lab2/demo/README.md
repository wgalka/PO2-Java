---
layout: default
title: "Layout Explorer — działająca aplikacja JavaFX"
---

# Layout Explorer — działająca aplikacja JavaFX

Otwórz [pom.xml](pom.xml) jako projekt Maven w IntelliJ IDEA. Ustaw **JDK 27 GA**
dla Project SDK i Maven Runner, zsynchronizuj zależności i uruchom
**Maven → Plugins → javafx → javafx:run**. Projekt używa **JavaFX 27**,
bez FXML i bez `module-info.java`. Cała logika demonstracji znajduje się
w [LayoutExplorer.java](src/main/java/pl/uczelnia/lab2/LayoutExplorer.java).

## Obsługa

1. Wybierz przykład z listy: `HBox`, `VBox`, `FlowPane`, `TilePane`, `BorderPane`,
   `GridPane`, `StackPane`, `AnchorPane`, `ScrollPane / Accordion` lub `Zdarzenia`.
2. Suwakiem zmień szerokość próbki. To rozmiar kontenera w jednostkach logicznych,
   nie zmiana DPI monitora. Możesz również zmieniać rozmiar okna aplikacji.
3. Porównaj `Spacing / gap` z `Padding`. Odstępy między dziećmi zmieniają się
   w HBox, VBox, FlowPane, TilePane i GridPane; padding zmienia się we wszystkich próbkach.
4. Wyrównanie zmienia się w HBox, VBox, FlowPane, TilePane i GridPane.
   Przełącznik wzrostu ogranicza lub zwalnia maksymalny rozmiar pola w HBox/GridPane
   albo wysokość TextArea w VBox; priorytet rodzica pozostaje `ALWAYS`.
5. Włącz ciemny motyw i większy tekst. Zmiana motywu nie tworzy kontrolek od nowa,
   więc wpisane dane pozostają. Motyw jest zapamiętywany między uruchomieniami.
6. W StackPane przełącz nakładkę, w Accordion rozwiń drugą sekcję,
   a w przykładzie Zdarzenia porównaj dziennik z `consume()` i bez niego.

Zmiana rodzaju przykładu tworzy nową próbkę i zeruje jej dane — służy porównywaniu
niezależnych układów. Zmiana szerokości, motywu i czcionki zachowuje bieżącą próbkę.
Nie wszystkie regulatory mają sens dla każdego kontenera: BorderPane nie ma
własności `spacing`, a AnchorPane pozycjonuje według kotwic.

Próbka ma demonstracyjną wysokość 300 jednostek. Jeżeli przy ekstremalnych
odstępach zabraknie miejsca, obserwuj ograniczenia kontenera i użyj przewijania
obszaru pokazu. W docelowym formularzu ustal minimalny użyteczny rozmiar oraz
adaptacyjny układ zgodnie z [wymaganiami GUI](../../jakosc-gui.html).

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

W kodzie dodaj własny widok, np. formularz wyboru zmiennych z przyciskiem analizy.
Przewidź rezultat przed uruchomieniem. Pełne wyjaśnienia i diagramy są w [Lab 2](../README.html).

## Kontrola techniczna

W konfiguracji Maven w IntelliJ uruchom:

```text
clean compile javafx:run -Djavafx.args=--verify
```

Tryb `--verify` tworzy sceny bez pokazywania okna, stosuje CSS, przelicza rozmiary
dla obu motywów i dwóch szerokości oraz sprawdza zawijanie FlowPane.
Wymaga środowiska obsługującego JavaFX graphics; nie jest backendem headless.
Nie zastępuje ręcznych testów klawiatury, ostrości na monitorze ani macierzy HiDPI.
Bez `--verify` aplikacja uruchamia okno do interaktywnej pracy.
