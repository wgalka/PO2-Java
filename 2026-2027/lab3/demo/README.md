---
layout: default
title: "Aplikacja wskaźników makroekonomicznych — JDBC i SQLite"
---

# Aplikacja wskaźników makroekonomicznych — JDBC i SQLite

Projekt Maven można [pobrać jako ZIP](projekt-do-modyfikacji.zip), rozpakować
i otworzyć w IntelliJ IDEA jako folder zawierający `pom.xml`. Należy pracować
na kopii i wykonać rozszerzenia opisane w [Lab 3](../README.html).

Plik [pom.xml](pom.xml) należy otworzyć jako projekt Maven w IntelliJ IDEA.
Następnie trzeba wybrać **JDK 27 GA** dla Project SDK i Maven Runner,
zsynchronizować zależności oraz uruchomić `Main.main` z
[Main.java](src/main/java/pl/uczelnia/lab3/Main.java).

Aplikacja demonstruje relacyjny model danych ekonomicznych (`indicator` oraz `observation`), obsługę kluczy obcych (`PRAGMA foreign_keys = ON`), transakcje atomowe z wycofaniem (`rollback`) oraz integrację z tabelą GUI w bibliotece Swing z obsługą motywów FlatLaf.

## Funkcjonalności demonstracyjne

1. **Inicjalizacja i dane początkowe:** Przy pierwszym uruchomieniu aplikacja tworzy bazę `data/economy.db`, nakłada schemat SQL i zasila bazę początkowymi szeregami (stopa bezrobocia i inflacja CPI).
2. **Przeglądanie i filtrowanie:** Rozwijana lista pozwala filtrować obserwacje według wybranego wskaźnika; tabela `JTable` odświeża wynik zapytaniem `JOIN`.
3. **Formularz z walidacją:** Pola na dole okna pozwalają dopisać nową obserwację. Niepoprawne formaty liczb oraz naruszenia ograniczeń bazy są przechwytywane i wyświetlane w oknach dialogowych bez rzucania niespójnych wyjątków na konsolę.
4. **Test atomowości i wycofania transakcji:** Przycisk „Test wycofania transakcji” wywołuje operację wsadową celowo zawierającą duplikat klucza biznesowego `(indicator_id, period, region)`. Aplikacja wycofuje transakcję — żaden rekord z paczki nie trafia do bazy.
5. **Obsługa motywów i HiDPI:** Przycisk „Przełącz motyw” zmienia styl FlatLaf między jasnym a ciemnym i utrwala wybór w rejestrze preferencji Javy (`Preferences`).

## Weryfikacja techniczna

W trybie weryfikacji aplikacja wykonuje testy w pamięci (`jdbc:sqlite::memory:`):

1. Otworzyć **Run → Edit Configurations** i wybrać konfigurację typu **Application**
   dla klasy `pl.uczelnia.lab3.Main` (można ją utworzyć strzałką przy `main`).
2. W polu **Program arguments** wpisać `--verify`. Jako moduł classpath należy
   wskazać projekt `lab3`, a jako **Working directory** — folder z jego `pom.xml`.
3. Uruchomić konfigurację i odczytać wynik w konsoli **Run**. IntelliJ dołącza
   zależności Maven wybranego modułu.
4. Przed uruchomieniem GUI usunąć `--verify` z argumentów.

Test automatyczny sprawdza:

- poprawność utworzenia schematu i relacji,
- operacje CRUD w DAO,
- działanie transakcji atomowej z błędem i potwierdzenie wycofania (`rollback`),
- obliczenia zmian okres do okresu na pobranych danych.
