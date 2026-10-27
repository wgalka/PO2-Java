---
layout: default
title: "Mapowanie obiektowo-relacyjne (Hibernate 6) — IntelliJ IDEA"
---

# Mapowanie obiektowo-relacyjne (Hibernate 6) — IntelliJ IDEA

Projekt demonstracyjny można [pobrać jako ZIP](projekt-do-modyfikacji.zip),
rozpakować i otworzyć w IntelliJ IDEA jako folder zawierający `pom.xml`.
Należy pracować na kopii i wykonać rozszerzenia opisane w [Lab 4](../README.html).

Plik [pom.xml](pom.xml) należy otworzyć jako projekt Maven w IntelliJ IDEA.
Następnie trzeba ustawić **JDK 27 GA** dla Project SDK i Maven Runner,
zsynchronizować zależności i uruchomić `Main.main` z
[Main.java](src/main/java/pl/uczelnia/lab4/Main.java).

Projekt demonstruje nowoczesne mapowanie ORM z użyciem Hibernate 6 i Jakarta Persistence 3.1
w domenie analizy danych ekonomicznych:
- encja nadrzędna `EconomicDataset` (zbiór wskaźników),
- powiązane encje `Observation` (obserwacje w relacji `@OneToMany` / `@ManyToOne`),
- encja `AnalysisReport` (raporty z modeli analitycznych),
- projekcja HQL do rekordów DTO (`DatasetSummaryDto`), zapobiegająca problemowi N+1 i `LazyInitializationException`,
- transakcje atomowe z wycofaniem (`rollback`),
- interfejs graficzny Swing z obsługą motywów FlatLaf (jasny/ciemny).

## Weryfikacja techniczna

Aplikację można uruchomić w trybie automatycznego testu w pamięci (`jdbc:h2:mem:...`):

1. Otworzyć **Run → Edit Configurations** i wybrać konfigurację typu **Application**
   dla klasy `pl.uczelnia.lab4.Main` (można ją utworzyć strzałką przy `main`).
2. W polu **Program arguments** wpisać `--verify`. Jako moduł classpath należy
   wskazać projekt `lab4`, a jako **Working directory** — folder z jego `pom.xml`.
3. Uruchomić konfigurację i odczytać wynik w konsoli **Run**. IntelliJ dołącza
   zależności Maven wybranego modułu.
4. Przed uruchomieniem GUI usunąć `--verify` z argumentów.

Test automatyczny weryfikuje:

- poprawne zbudowanie `SessionFactory`,
- automatyczne wygenerowanie schematu DDL przez Hibernate (`hbm2ddl.auto`),
- kaskadowy zapis encji powiązanych (`CascadeType.ALL`),
- zapytanie agregujące HQL z projekcją `select new ...`,
- wycofanie transakcji w przypadku naruszenia więzów unikalności.
