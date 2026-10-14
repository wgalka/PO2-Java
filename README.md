# Programowanie obiektowe - Java

Materiały do przedmiotu **Programowanie obiektowe** dla kierunku Informatyka i ekonometria.

## Najważniejsze informacje

| Element | Wartość |
|---|---|
| Rok akademicki | 2026/2027 |
| Kierunek | Informatyka i ekonometria |
| Semestr | III |
| Zajęcia | 14 spotkań, łącznie 30 godzin laboratoryjnych |
| Forma zaliczenia | Projekt z obroną |
| Środowisko | IntelliJ IDEA, JDK 27 GA, JavaFX 27, Maven — stan na 13.10.2026 |

## Laboratoria

### Semestr III - GUI i dostęp do danych

Instrukcje są pogrupowane tematycznie. Sześć dokumentów obejmuje łącznie 14
spotkań i 30 godzin zajęć laboratoryjnych.

| Lab | Temat | Liczba spotkań | Status |
|---|---|---:|---|
| [Lab 1](./2026-2027/lab1/README.md) | Swing: okna, menedżery układu, formularze, tabele i rysowanie | 3 | Dostępne |
| [Lab 2](./2026-2027/lab2/README.md) | JavaFX: scena, formularze, tabele, wykresy i FXML/MVC | 4 | Dostępne |
| [Lab 3](./2026-2027/lab3/README.md) | JDBC: SQL, DAO, CRUD, relacje, transakcje i GUI | 3 | Dostępne |
| [Lab 4](./2026-2027/lab4/README.md) | Hibernate: encje, relacje, HQL i transakcje | 2 | Dostępne |
| [Lab 5](./2026-2027/lab5/README.md) | JSP i Servlet API | 1 | Dostępne |
| [Lab 6](./2026-2027/lab6/README.md) | Integracja projektu desktopowego i obrona | 1 | Dostępne |

- [Plan laboratoriów](./2026-2027/plan.md)
- [Wymagania projektu i kryteria oceny](./2026-2027/projekt/README.md)

## Zasady pracy

Zacznij od [konfiguracji IntelliJ IDEA i Maven](./2026-2027/srodowisko.md).
Każdy lab zawiera plan spotkań, diagramy Mermaid, punkty kontrolne i opis materiałów do oddania.
Diagramy pokazują zależności i zachowanie; dokładny wygląd GUI sprawdzaj w działającej aplikacji.
W Lab 1 dostępna jest [galeria działających komponentów Swing](./2026-2027/lab1/demo/README.md).
W Lab 2 uruchom [Layout Explorer JavaFX](./2026-2027/lab2/demo/README.md),
aby porównać kontenery, zmieniać ich parametry i obserwować obsługę zdarzeń.

Instrukcje uwzględniają [adaptacyjny układ, HiDPI i jasny/ciemny motyw](./2026-2027/jakosc-gui.md).
Lab 1 zawiera responsywny kalkulator, Lab 2 adaptacyjny panel danych, a kolejne
laby i projekt zachowują te wymagania. Odbiór GUI obejmuje testy skalowania 100%, 150% i 200%.

Instrukcje zawierają kod startowy i zadania. Kod należy przepisać, uruchomić, zrozumieć i rozszerzyć, a nie tylko skopiować. Do każdego zadania warto przygotować krótką demonstrację działania oraz zadbać o obsługę błędnych danych.
