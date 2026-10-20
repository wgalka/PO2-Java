---
layout: default
title: "Responsywność, HiDPI i motywy — wymagania oraz testy"
---

# Responsywność, HiDPI i motywy — wymagania oraz testy

Wymagania obowiązują w ćwiczeniach GUI Lab 1–4 i w projekcie końcowym. Lab 5 stosuje ich odpowiednik dla strony WWW. Nie wystarcza pokazanie okna w jednym rozmiarze i jednym motywie.

## Trzy różne zagadnienia

- **Adaptacyjny układ:** wykorzystuje dostępne miejsce, a przy małej szerokości zmienia rozmieszczenie lub zwija panele pomocnicze. Wszystkie podstawowe akcje pozostają dostępne.
- **HiDPI:** interfejs pozostaje czytelny i ostry przy skalowaniu systemowym. Jednostka logiczna layoutu nie musi odpowiadać jednemu fizycznemu pikselowi monitora.
- **Responsywność działania:** odczyt pliku, zapytanie i analiza nie blokują wątku GUI. Powiększanie okna nie rozwiązuje blokowania aplikacji.

Zmiana motywu IntelliJ nie zmienia automatycznie motywu tworzonej aplikacji. W aplikacji implementujemy osobny wybór **Jasny / Ciemny**. Dopasowanie do motywu systemu jest rozszerzeniem; sam motyw systemowy nie gwarantuje ciemnego wyglądu Swing ani JavaFX.

## Wymagany wygląd

1. Korzystaj z menedżerów układu i kontenerów. Nie ustawiaj wszystkich pozycji przez `setBounds` ani `layoutX/layoutY`.
2. Ustal hierarchię: nagłówek, treść, główna akcja, akcje pomocnicze i status. Stosuj spójne odstępy, wyrównanie etykiet oraz ograniczoną paletę kolorów.
3. Pozostaw czytelny rozmiar tekstu. Przy małym oknie przeorganizuj układ; nie zmniejszaj całego interfejsu transformacją skali. Długie dane mogą być przewijane, ale podstawowe akcje nie mogą znikać.
4. Korzystaj z domyślnych rozmiarów kontrolek i metryk czcionek. Wysokość wiersza tabeli ma mieścić tekst wraz z odstępami także przy większej czcionce. Nie mnoż ręcznie wszystkich wymiarów przez DPI — framework już obsługuje skalowanie.
5. Preferuj ikony wektorowe lub warianty rastrowe o większej rozdzielczości. Samo powiększenie małego PNG nie zapewnia ostrości. W Swing własne rysowanie wykonuj na kopii `Graphics2D`, zachowując otrzymaną transformację i wywołując `dispose()` tylko na kopii.
6. Motyw obejmuje również menu, okna dialogowe, pola, zaznaczenie tabeli, tooltipy, fokus klawiatury, tekst nieaktywny i wykresy. Nie wystarczy zmienić tła głównego panelu.
7. Kolor nie jest jedynym nośnikiem informacji: błąd ma tekst, a serie wykresu etykiety oraz rozróżnialne symbole lub style linii. Nie ustawiaj stałego czarnego tekstu na tle zależnym od motywu.
8. Przełączenie motywu zachowuje dane formularza, wynik, filtr i zaznaczenie. Zapamiętaj wybór między uruchomieniami, np. przez `java.util.prefs.Preferences`.

## Macierz testów

W Windows zmieniaj **Ustawienia → System → Ekran → Skala**. Po zmianie uruchom aplikację ponownie, jeśli środowisko tego wymaga. Sama zmiana rozdzielczości monitora nie jest testem skalowania DPI.

Wykonaj wszystkie kombinacje poniżej — 12 przypadków dla aplikacji desktopowej:

| Skala systemowa | Jasny, wąskie okno | Jasny, szerokie okno | Ciemny, wąskie okno | Ciemny, szerokie okno |
|---|---|---|---|---|
| 100% | wynik + uwagi | wynik + uwagi | wynik + uwagi | wynik + uwagi |
| 150% | wynik + uwagi | wynik + uwagi | wynik + uwagi | wynik + uwagi |
| 200% | wynik + uwagi | wynik + uwagi | wynik + uwagi | wynik + uwagi |

Wąskie okno oznacza zadeklarowany minimalny użyteczny rozmiar; szerokie — rozmiar powyżej progu przełączenia układu. Zapisz w raporcie wymiary logiczne obszaru treści, JDK, system i skalę. Dobierz próg tak, aby oba warianty można było pokazać na dostępnym monitorze. Aplikacja musi mieścić się w jego obszarze roboczym także przy 200%.

Dla każdego przypadku sprawdź: pełny tekst przycisków, widoczny wynik, menu i dialog błędu, zaznaczenie i fokus, ostrość ikon oraz czytelność wykresu. Zwiększ dodatkowo czcionkę aplikacji o około 25% i sprawdź brak przycięcia etykiet i wierszy. Rozmiar okna zmieniaj także płynnie przez próg układu, nie tylko na początku testu.

Jeżeli masz dwa monitory o różnych skalach, przenieś między nimi działającą aplikację. Jeżeli sprzęt lub system nie umożliwia któregoś testu, wpisz **niesprawdzone — powód**, zamiast wyniku pozytywnego; brakujący przypadek wykonaj na stanowisku laboratoryjnym. Wymuszenie skali parametrem JVM jest testem pomocniczym, nie potwierdzeniem działania na rzeczywistych monitorach o różnym DPI.

## Dowody do oddania

Dołącz wypełnioną macierz, listę poprawionych usterek i cztery zrzuty: wąskie/jasne, szerokie/jasne, wąskie/ciemne i szerokie/ciemne; co najmniej dwa przy 200%. Opisz skalę przy każdym obrazie. Render PNG lub powiększenie zrzutu nie zastępuje testu działającego GUI. Dołącz wynik próby przełączenia motywu w trakcie wpisywania danych i potwierdzenie odtworzenia ustawienia po restarcie.
