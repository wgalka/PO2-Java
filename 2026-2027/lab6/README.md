---
layout: default
title: "Lab 6 - Projekt: integracja aplikacji desktopowej i obrona"
---

# Lab 6 - Projekt: integracja aplikacji desktopowej i obrona

## Integracja aplikacji desktopowej i przygotowanie obrony

Projekt ma być aplikacją desktopową w języku Java z GUI, relacyjną bazą danych oraz
analizą danych ekonomicznych lub ekonometrycznych. Należy rozwijać projekt
przygotowany w poprzednich laboratoriach zamiast rozpoczynać od pustego szkieletu.
Pełny zakres i punktacja znajdują się w [wymaganiach projektu](../projekt/README.html).
Projekt należy uruchamiać i budować w [IntelliJ IDEA](../srodowisko.html).

Należy połączyć rozwijany interfejs Swing z Lab 1 albo JavaFX z Lab 2 z warstwą
JDBC/DAO z Lab 3 albo Hibernate z Lab 4. Integracja wymaga dostosowania wspólnego
modelu domeny i serwisów, a następnie dodania analizy i wykresów zgodnych
z wybranym tematem. Nie trzeba stosować obu bibliotek GUI ani obu metod persystencji.
Dane demonstracyjne i skrypt inicjalizacji należy dołączyć do repozytorium,
np. w `src/main/resources/db/` lub `data/`. Po przygotowaniu zależności pełny
scenariusz obrony musi działać bez Internetu i bez wywołań zewnętrznego API.

## Integracja funkcji aplikacji

1. Zdefiniować problem ekonomiczny, użytkownika i pytanie analityczne.
2. Przygotować lokalny plik danych demonstracyjnych albo skrypt inicjalizujący bazę.
3. Połączyć widoki GUI z serwisem, repozytorium JDBC/Hibernate i bazą.
4. Dodać statystyki opisowe, wybraną metodę analizy i wykres.
5. Obsłużyć błędne dane, pusty wynik i brak połączenia z bazą.
6. Przygotować ERD, instrukcję uruchomienia, opis metody i interpretację wyników.
7. Zademonstrować pełny przepływ od importu danych do wyniku analizy.

## Lista kontrolna przed oddaniem

- [ ] układ adaptuje się do małego i dużego okna, a wszystkie podstawowe akcje pozostają dostępne;
- [ ] jasny i ciemny motyw obejmują formularze, menu, dialogi, tabele oraz wykresy;
- [ ] przełączenie motywu nie gubi danych, a wybór jest odtwarzany po restarcie;
- [ ] wykonano [macierz testów 100%, 150%, 200%](../jakosc-gui.html) i opisano wyniki;
- [ ] tekst, ikony, fokus i zaznaczenie są czytelne; większa czcionka nie ucina treści;

- [ ] aplikacja uruchamia się na czystym środowisku zgodnie z README;
- [ ] projekt działa bez dostępu do Internetu;
- [ ] dane demonstracyjne są dołączone do repozytorium;
- [ ] baza ma klucze, relacje i ograniczenia integralności;
- [ ] GUI nie zawiera zapytań SQL ani obliczeń biznesowych;
- [ ] analiza ma opis zmiennych, wzór/założenia i interpretację;
- [ ] wykresy mają podpisane osie i jednostki;
- [ ] dokumentacja zawiera ERD, instrukcję użytkownika i ograniczenia;
- [ ] student potrafi wyjaśnić przepływ danych i uruchomić testowy scenariusz.

## Przygotowanie odpowiedzi do obrony

1. Dlaczego wybrano ten model danych i metodę analizy?
2. Jak aplikacja reaguje na brak lub błędną wartość?
3. Które klasy pokazują hermetyzację, polimorfizm i separację warstw?
4. Jakie zapytania wykonuje aplikacja i gdzie są otwierane transakcje?
5. Jak zinterpretować najważniejszy wynik ekonomiczny?

## Podział aplikacji analitycznej na warstwy

```mermaid
flowchart TD
    U[Użytkownik] --> G["GUI: dane, parametry, wykresy"]
    G --> C[Kontroler]
    C --> S["Serwis: walidacja i koordynacja"]
    S --> I["Importer lokalnych danych"]
    I --> F[Plik CSV]
    S --> R[Repozytorium]
    R --> B[(Baza lokalna)]
    S --> A["Moduł analizy: statystyki i model"]
    A --> W["Wynik: parametry, miary, ostrzeżenia"]
    W --> C
    C --> G
    S --> E[Eksport raportu]
```

Obliczenia przyjmują dane i konfigurację, a zwracają wynik; nie powinny odczytywać wartości bezpośrednio z pól GUI. Pozwala to sprawdzać metodę analityczną bez uruchamiania okna.

## Model danych — przykład do dostosowania

```mermaid
erDiagram
    DATASET ||--o{ OBSERVATION : zawiera
    DATASET ||--o{ ANALYSIS : analizowany_przez
    DATASET {
        long id PK
        string name
        string source
        string units
    }
    OBSERVATION {
        long id PK
        long dataset_id FK
        string period
        string region
        double x
        double y
    }
    ANALYSIS {
        long id PK
        long dataset_id FK
        string method
        string parameters
        string result
    }
```

To minimalny przykład szerokiego modelu. W projekcie nazwy `x` i `y` należy
zastąpić zmiennymi o znaczeniu ekonomicznym, zapisać ich jednostki i dostosować
liczbę do wymagań projektu. Trzeba zapewnić unikalność identyfikującą obserwację.
Analiza powinna zapamiętywać filtr i wersję danych albo ich migawkę, aby późniejsza
edycja zbioru nie zmieniała znaczenia zapisanego wyniku.

## Przygotowanie scenariusza demonstracji

```mermaid
flowchart LR
    A[Uruchomienie] --> B[Wczytanie danych demo]
    B --> C[Walidacja i zapis]
    C --> D[Wybór zmiennych i zakresu]
    D --> E[Obliczenie modelu]
    E --> F[Wykres i interpretacja]
    F --> G[Eksport raportu]
    G --> H[Restart i odczyt zapisanych danych]
```

Demonstracja powinna trwać około 8–10 minut, o ile prowadzący nie ustali innego
czasu. Należy przedstawić problem ekonomiczny i źródło lokalnego zbioru, a następnie
pełny przepływ danych. W dalszej części trzeba wywołać błąd danych i pokazać jego
obsługę. Do omówienia podczas obrony należy przygotować w IntelliJ klasy
kontrolera, serwisu, repozytorium i analizy.

## Weryfikacja poprawności obliczeń

Przykład kontrolny OLS z jedną zmienną: `x = [1, 2, 3, 4]`, `y = [3, 5, 7, 9]`. Oczekiwany model to `y = 1 + 2x`, średnia `y = 6`, suma kwadratów reszt `SSE = 0` i `R² = 1`. Dla obliczeń zmiennoprzecinkowych porównuj z tolerancją, np. `1e-9`.

Ten mały sztuczny zbiór służy sprawdzeniu implementacji, ale nie zastępuje danych
ekonomicznych. Przy regresji wielorakiej należy dodać test znanych parametrów
dla pełnorządowej macierzy oraz test współliniowości. Macierzy nie należy
odwracać bez kontroli osobliwości.

| Przypadek | Oczekiwane zachowanie |
|---|---|
| brak obserwacji po filtrowaniu | brak próby dopasowania, komunikat |
| wszystkie wartości `x` jednakowe | informacja o niemożności oszacowania nachylenia |
| stałe `y` | jawna obsługa nieokreślonego standardowego `R²`, bez prezentacji `NaN` jako wyniku |
| brak wartości lub duplikat | ustalona polityka, raport i liczba pominiętych rekordów |
| błąd w połowie importu | wycofanie importu albo jawnie opisany tryb częściowy |
| dłuższa analiza | GUI reaguje, przycisk nie uruchamia wielu analiz równolegle |
| błąd eksportu | komunikat z możliwością wyboru innej lokalizacji |

Interpretacja powinna odróżniać zależność statystyczną od przyczynowości.
Przy prognozie należy zachować kolejność czasu i oceniać wynik na danych
niewykorzystanych do dopasowania. Trzeba opisać jednostki błędu i ograniczenia
małego zbioru.

## Przygotowanie pakietu do oddania

Do materiałów GUI dołącz także [raport HiDPI i motywów](../jakosc-gui.html):
12 przypadków, cztery zrzuty obu układów i motywów oraz opis ograniczeń sprzętowych,
jeżeli wystąpiły. Podczas obrony należy pokazać zmianę szerokości i przełączyć
motyw przy załadowanych danych. Stan aplikacji i wynik analizy muszą zostać zachowane.

- projekt Maven z kodem i zasobami; `clean verify` w IntelliJ kończy się poprawnie;
- lokalne dane demonstracyjne, opis źródła i sposobu odtworzenia bazy;
- README z JDK, uruchomieniem, ścieżkami danych i scenariuszem demonstracji;
- diagram architektury i ERD w blokach Mermaid;
- wyniki testów obliczeń i testu wycofania transakcji;
- zrzuty rzeczywistej aplikacji: tabela z filtrem, wybór analizy, wynik z wykresem, obsłużony błąd;
- przykładowy wyeksportowany raport z interpretacją i ograniczeniami.

Przed oddaniem należy otworzyć kopię projektu z `pom.xml` w IntelliJ, przygotować
zależności i uruchomić scenariusz offline. Katalogów `target`, `.idea`, haseł
ani przypadkowych danych prywatnych nie należy dołączać. Zrzuty ekranu
dokumentują działanie, ale nie zastępują uruchamialnego projektu.
