# Belák Ádám szakdolgozat
Ez a projekt az ELTE Informatika karán írt szakdolgozatom LaTeX kódja.

## Használat
Ha ebből szeretnél kiindulni, akkor az [information.tex](information.tex) fájlban
írd át az adatokat a saját szakdolgozatod szerint.

A különböző fejezeteket különböző mappákba helyeztem el és mindent ami ahhoz a
fejezethez tartozik oda érdemes eltárolni.

A pdf generáláshoz készítettem egy egyszerű scriptet, ami létrehoz egy  <kbd>output</kbd>
mappát és abba generálja a pdf fájlt és a generálás során keletkezett egyéb fájlokat.

Ha még nem rendelkezne futási joggal, add meg neki a következő paranccsal:

```bash
  chmod +x compile.sh
```

Ezután a scriptet az alábbi kóddal tudod lefuttatni:

```bash
  ./compile.sh
```
A `.gitignore` fájl tiltja, hogy tárolásra kerüljön az <kbd>output</kbd> mappa
tartalma, így a generált pdf fájl sem lesz hozzáadva a gitben kezelt fájlokhoz.
Ha mégis el szeretnél tárolni egy elkészült pdf fájlt, akkor a parancsot használd
a `--create-release` paraméterrel:

```bash
  ./compile.sh --create-release
```

Ennek hatására a <kbd>releases</kbd> mappában létrejön egy új pdf fájl, ami el van
látva az aktuális rendszeridővel és ezt már nyugodtan commitolhatjátok.
Ha az aktuális időpont nélkül szeretnél létrehozni egy verziót, ami ugyanazt a
fájlt írja felül minden egyes alkalommal, használhatod a `--update-latest`
kapcsolót:

```bash
  ./compile.sh --update-latest
```

Melynek eredményéül a <kbd>releases</kbd> mappában módosul, vagy ha még nem létezne,
akkor létrejön a <kbd>thesis.pdf</kbd> nevű dokumentum.  
A két kapcsoló tetszőleges sorrendben, együtt is használható. 

## Helyesírás-ellenőrzés
Mivel nem minden eszköz képes a LaTeX dokumentumok helyesírásának ellenőrzésére,
készítettem egy scriptet, ami ezt a problémát megoldja. Amennyiben még nem használtad
a scriptet, először adj neki futtatási jogot a következő paranccsal:

```bash
  chmod +x spellcheck.sh
```

Ezután a script futtatható lesz és használhatod egyetlen fájl, vagy akár a teljes
projekt ellenőrzésére is. Például a

```bash
  ./spellcheck introduction/index.tex
```

parancs, az <kbd>introduction</kbd> mappában található <kbd>index.tex</kbd> fájl
fogja ellenőrizni. A script csak a `.tex` kiterjesztéssel rendelkező fájlokat
vizsgálja, így az a script hívása során elhagyható.  
Ha szeretnéd megőrini a helyesírás előtti állapotot (bár git miatt felesleges),
akkor azt a `--store-prev` paraméterrel teheted meg:

```bash
  ./spellcheck introduction/index --store-prev
```

Így ellenőrzés után, ha változás volt, létrejön egy <kbd>introduction.index.tex.bak</kbd>
fájl az ellenőrzés előtti állapottal.

Amennyiben a teljes szakdolgozaton szeretnél helyesírás ellenőrzést végezni, azt
a `--all` kapcsolóval teheted meg:

```bash
  ./spellcheck --all
```

Ennek hatására a script az összes `.tex` kiterjesztésű fájlon ellenőrzést véget,
amik az <kbd>introduction</kbd>, <kbd>user-documentation</kbd>,
<kbd>developer-documentation</kbd>, <kbd>bibliography</kbd> mappákban találhatóak.  
Itt is érvényes, hogy ha meg szeretnéd őrizni, az előző verziót használd, a
`--store-prev` kapcsolót:

```bash
  ./spellcheck --all --store-prev
```

Figyelem! A paraméterek sorrendje kötött!

## Felhasznált anyagok
A projekt alapját az [ELTE-LaTeX-Thesis-Base](https://github.com/shdnx/ELTE-LaTeX-Thesis-Base)
képzi, melyet saját ízlésem szerint átszerveztem, de állítólag megfelel az ELTE-IK
formai követelményeknek.
