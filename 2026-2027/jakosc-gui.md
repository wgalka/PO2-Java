---
layout: default
title: "Responsywność, HiDPI i motywy — wymagania oraz testy"
---

# Responsywność, HiDPI i motywy — wymagania oraz testy

Wymagania obowiązują w ćwiczeniach GUI Lab 1–4 i w projekcie końcowym. Lab 5 stosuje ich odpowiednik dla strony WWW. Nie wystarcza pokazanie okna w jednym rozmiarze i jednym motywie.

## Wymagania dotyczące układu, skalowania i responsywności działania

- **Adaptacyjny układ:** wykorzystuje dostępne miejsce, a przy małej szerokości zmienia rozmieszczenie lub zwija panele pomocnicze. Wszystkie podstawowe akcje pozostają dostępne.
- **HiDPI:** interfejs pozostaje czytelny i ostry przy skalowaniu systemowym. Jednostka logiczna layoutu nie musi odpowiadać jednemu fizycznemu pikselowi monitora.
- **Responsywność działania:** odczyt pliku, zapytanie i analiza nie blokują wątku GUI. Powiększanie okna nie rozwiązuje blokowania aplikacji.

Aplikacja powinna domyślnie działać w trybie **Systemowy**: dopasowywać jasny lub
ciemny wygląd do ustawień systemu i reagować na ich zmianę podczas pracy. Menu
motywu powinno też pozwalać wybrać **Jasny** lub **Ciemny**; taki wybór ręczny
obowiązuje do powrotu do trybu **Systemowy**. Należy zapamiętać wybrany tryb.

JavaFX 27 udostępnia preferencję systemową przez
[`Platform.Preferences.colorSchemeProperty()`](https://openjfx.io/javadoc/27/javafx.graphics/javafx/application/Platform.Preferences.html),
która aktualizuje się, gdy system zgłosi zmianę. FlatLaf dostarcza jasną i ciemną
paletę, ale nie monitoruje przenośnie motywu systemu; aplikacja Swing musi
zapewnić taki odczyt dla wspieranej platformy.

## Wymagany wygląd

1. Stosować menedżery układu i kontenery; nie ustawiać wszystkich pozycji przez `setBounds` ani `layoutX/layoutY`.
2. Ustalić hierarchię nagłówka, treści, akcji głównej, akcji pomocniczych i statusu. Zachować spójne odstępy, wyrównanie etykiet i ograniczoną paletę kolorów.
3. Zachować czytelny rozmiar tekstu. Przy małym oknie należy przeorganizować układ, a nie zmniejszać cały interfejs transformacją skali. Długie dane można przewijać, ale podstawowe akcje muszą pozostać dostępne.
4. Korzystać z domyślnych rozmiarów kontrolek i metryk czcionek. Wysokość wiersza tabeli powinna mieścić tekst wraz z odstępami także przy większej czcionce. Nie należy ręcznie mnożyć wszystkich wymiarów przez DPI — framework obsługuje skalowanie.
5. Preferować ikony wektorowe lub warianty rastrowe o większej rozdzielczości. Samo powiększenie małego PNG nie zapewnia ostrości. Własne rysowanie w Swing należy wykonywać na kopii `Graphics2D`, zachowując otrzymaną transformację i wywołując `dispose()` tylko na kopii.
6. Motyw powinien obejmować menu, okna dialogowe, pola, zaznaczenie tabeli, tooltipy, fokus klawiatury, tekst nieaktywny i wykresy; samo zmienienie tła głównego panelu nie wystarcza.
7. Kolor nie powinien być jedynym nośnikiem informacji: błąd wymaga tekstu, a serie wykresu — etykiet i rozróżnialnych symboli lub stylów linii. Stałego czarnego tekstu nie należy umieszczać na tle zależnym od motywu.
8. Zmiana motywu — automatyczna lub ręczna — powinna zachować dane formularza, wynik, filtr i zaznaczenie. Należy zapamiętać tryb `SYSTEM`, `LIGHT` albo `DARK`, np. przez `java.util.prefs.Preferences`, a nie sam kolor wynikający z aktualnych ustawień systemu.

## Macierz testów

W systemie Windows skalę należy zmienić w **Ustawienia → System → Ekran → Skala**.
Po zmianie aplikację należy uruchomić ponownie, jeśli wymaga tego środowisko.
Sama zmiana rozdzielczości monitora nie jest testem skalowania DPI.

Należy wykonać wszystkie kombinacje poniżej — 12 przypadków dla aplikacji desktopowej:

| Skala systemowa | Jasny, wąskie okno | Jasny, szerokie okno | Ciemny, wąskie okno | Ciemny, szerokie okno |
|---|---|---|---|---|
| 100% | wynik + uwagi | wynik + uwagi | wynik + uwagi | wynik + uwagi |
| 150% | wynik + uwagi | wynik + uwagi | wynik + uwagi | wynik + uwagi |
| 200% | wynik + uwagi | wynik + uwagi | wynik + uwagi | wynik + uwagi |

W kolumnach **Jasny** i **Ciemny** aplikacja musi mieć wybrany tryb
**Systemowy**. Dodatkowo należy zmienić motyw systemu podczas działania
aplikacji i sprawdzić, że interfejs reaguje bez restartu. Po ręcznym wybraniu
jasnego lub ciemnego wariantu zmiana ustawienia systemowego nie powinna go
nadpisywać; powrót do **Systemowego** ponownie włącza automatyczne dopasowanie.

Wąskie okno oznacza zadeklarowany minimalny użyteczny rozmiar; szerokie — rozmiar
powyżej progu przełączenia układu. W raporcie należy zapisać wymiary logiczne
obszaru treści, JDK, system i skalę. Próg należy dobrać tak, aby oba warianty
można było pokazać na dostępnym monitorze. Aplikacja musi mieścić się w jego
obszarze roboczym także przy 200%.

W każdym przypadku należy sprawdzić pełny tekst przycisków, widoczny wynik,
menu i dialog błędu, zaznaczenie i fokus, ostrość ikon oraz czytelność wykresu.
Czcionkę aplikacji trzeba dodatkowo zwiększyć o około 25% i sprawdzić, czy etykiety
i wiersze nie są przycinane. Rozmiar okna należy zmieniać płynnie przez próg
układu, a nie tylko na początku testu.

Jeżeli dostępne są dwa monitory o różnych skalach, działającą aplikację należy
przenieść między nimi. Gdy sprzęt lub system nie umożliwia któregoś testu,
w raporcie należy wpisać **niesprawdzone — powód**, zamiast wyniku pozytywnego;
brakujący przypadek trzeba wykonać na stanowisku laboratoryjnym. Wymuszenie skali
parametrem JVM jest testem pomocniczym, a nie potwierdzeniem działania na
rzeczywistych monitorach o różnym DPI.

## Pomocnicze wymuszenie skali w Windows

Jeżeli skali systemowej nie można zmienić, należy uruchomić osobne próby
z jedną z poniższych opcji JVM. Po zmianie parametru trzeba ponownie uruchomić
aplikację. Parametry dotyczą skalowania aplikacji, nie interfejsu IntelliJ.

| Skala wymuszona | Swing / FlatLaf na JDK | JavaFX na Windows |
|---|---|---|
| 100% | `-Dsun.java2d.uiScale=1.0` | `-Dglass.win.uiScale=100%` |
| 150% | `-Dsun.java2d.uiScale=1.5` | `-Dglass.win.uiScale=150%` |
| 200% | `-Dsun.java2d.uiScale=2.0` | `-Dglass.win.uiScale=200%` |

W konfiguracji typu **Application** należy otworzyć **Run → Edit Configurations**,
wybrać uruchamianą klasę i przez **Modify options → Add VM options** udostępnić
pole **VM options**. Należy wpisać w nim jeden parametr odpowiedni dla biblioteki
i badanej skali. Pole **Program arguments** służy argumentom `main`, nie opcjom JVM.

Przy uruchamianiu JavaFX przez **Maven → javafx:run** parametr należy dopisać
w `pom.xml` do istniejącego `configuration/options` wtyczki
`javafx-maven-plugin`, obok opcji dostępu natywnego:

```xml
<options>
    <option>--enable-native-access=javafx.graphics</option>
    <option>-Dglass.win.uiScale=150%</option>
</options>
```

Nie należy tworzyć drugiego bloku `options`. Wpisanie parametru wyłącznie
w opcjach JVM Maven Runnera nie zapewnia przekazania go do osobnego procesu
JavaFX. Po testach należy usunąć wymuszenie skali, zachowując dostęp natywny.

Są to właściwości implementacyjne opisane w
[dokumentacji skalowania FlatLaf](https://www.formdev.com/flatlaf/system-properties/)
i [kodzie platformy Windows JavaFX](https://github.com/openjdk/jfx/blob/master/modules/javafx.graphics/src/main/java/com/sun/glass/ui/win/WinApplication.java).
W raporcie należy zapisać „skala wymuszona JVM”, użyty parametr i skalę systemową.
Taka próba pomaga wykryć przycinanie kontrolek, ale nie potwierdza zachowania
natywnych dialogów ani przenoszenia okna między monitorami o różnym DPI.
Nie należy oznaczać jej jako wykonanego testu zmiany skali systemowej.

## Dowody do oddania

Do oddania należy dołączyć wypełnioną macierz, listę poprawionych usterek
i cztery zrzuty: wąskie/jasne, szerokie/jasne, wąskie/ciemne i szerokie/ciemne,
w tym co najmniej dwa przy 200%. Przy każdym obrazie trzeba opisać skalę.
Render PNG ani powiększenie zrzutu nie zastępują testu działającego GUI.
Należy dołączyć wynik próby przełączenia motywu podczas wpisywania danych
i potwierdzenie odtworzenia ustawienia po restarcie.
