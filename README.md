## osm19L_projekt1

**Przedmiot:** OSM

**Projekt:** 2

**Zadanie:** 7

**Temat:** Aplikacja do segmentacji obrazów medycznych w formacie DICOM wybranym
           algorytmem, np. rozrostu regionów (region-growing), k-średnich (k-means) lub wododziałowym
           (watershed). Efekt działania powinien być wyświetlany w osobnym panelu, tak aby jednocześnie
           widoczne były obraz oryginalny i obraz z nałożonymi wynikami segmentacji. Parametry
           przetwarzania wprowadzane są przez użytkownika za pomocą odpowiednich okien dialogowych

**Zespol:** Arkadiusz Majkowski, Dawid Cichocki

**Biblioteki:** ij.jar

**Uwagi dodatkowe:** Aplikacja oprócz segmentacji obrazów metodą k-średnich posiada także algorytm cannyego do detekcji krawędzi co zostało uzgodnione z 
prowadzącym.
    W ikonie "image" na górze po lewej stronie nastepuję wybór pliku na dwa sposoby poprzez wpisanie odpowiedniej ścieżki do niego oraz za pomocą filechooser czyli okna za pomocą którego możemy 
    wybrać obraz. Program jest w stanie odczytać pliki .dcm, .jpg i .png. Po wybraniu obrazu zostaje on umieszczony w oknie po lewej stronie. Po umieszczeniu pliku użytkownik może wykonać segmentację
    k-średnich lub detekcję krawędzi. W obydu przypadkach użytkownik może ingerować w sposób prezentacji danych po przez wprowadzanie innych zmiennych. Po wykonaniu i przedstawieniu obrazu po prawej
     stronie użytkownik za pomocą scrolla jest w stanie przybliżyć lub oddalić fragent obrazu.  

