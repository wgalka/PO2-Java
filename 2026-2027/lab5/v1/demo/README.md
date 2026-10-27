---
layout: default
title: "Aplikacja webowa wskaźników makroekonomicznych — JSP i Servlet API"
---

# Aplikacja webowa wskaźników makroekonomicznych — JSP i Servlet API

Projekt demonstracyjny można [pobrać jako ZIP](projekt-do-modyfikacji.zip),
rozpakować i otworzyć w IntelliJ IDEA jako folder zawierający `pom.xml`.
Należy pracować na kopii i wykonać rozszerzenia opisane w [Lab 5](../README.html).

Plik [pom.xml](pom.xml) należy otworzyć jako projekt Maven w IntelliJ IDEA.
Project SDK i Maven Runner trzeba ustawić na **JDK 27 GA**. Projekt buduje
archiwum `target/lab5.war`.

Aplikacja demonstruje webowy wzorzec Model-View-Controller (MVC):
- **Model:** rekord `EconomicIndicator` oraz serwis `IndicatorDataService`,
- **Controller:** servlet `IndicatorServlet` obsługujący metody `doGet` oraz `doPost` z walidacją,
- **View:** strona `WEB-INF/views/indicators.jsp` korzystająca z tagów JSTL oraz escapowania `<c:out>`,
- **Wzorzec PRG:** Post/Redirect/Get zabezpieczający przed wielokrotnym wysłaniem danych przy odświeżeniu strony,
- **Jakość GUI w webie:** domyślny motyw `Zgodnie z systemem` reagujący na zmianę preferencji systemowej bez przeładowania strony; opcjonalne ręczne warianty `Jasny` i `Ciemny` są zapamiętywane w `localStorage`.

## Uruchomienie na serwerze Tomcat 10.1

1. Uruchomić cel **Lifecycle → package** w oknie Maven IntelliJ; wynikiem powinien być plik `target/lab5.war`.
2. Pobrać i rozpakować serwer **Apache Tomcat 10.1** (zgodny z Jakarta EE 10 / Servlet 6.0). Sprawdzić, czy zmienna środowiskowa `JAVA_HOME` wskazuje **JDK 27 GA**.
3. Skopiować plik `lab5.war` do podkatalogu `webapps` w katalogu Tomcata.
4. Uruchomić serwer w terminalu:
   ```powershell
   ./bin/catalina.bat run
   ```
5. Otworzyć w przeglądarce adres:
   ```text
   http://localhost:8080/lab5/indicators
   ```
6. Zatrzymać serwer kombinacją klawiszy `Ctrl+C`.
