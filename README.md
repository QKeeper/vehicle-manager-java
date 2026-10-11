# Vehicles Manager

Консольное Java-приложение для управления коллекцией транспортных средств
(итоговое домашнее задание: уровень 1 + уровень 3).

Объект предметной области — `Vehicle` с полями `name`, `speed`, `year`.

## Требования

- Java 17 или новее.
- Запускать в **обычном терминале** (Windows Terminal, PowerShell, bash и т. п.).
  Программа очищает экран ANSI-последовательностями, а встроенная консоль IDE
  (например, IntelliJ IDEA) может показывать их как «мусор». В IDEA включите в настройках
  запуска опцию *Emulate terminal in output console*.

## Запуск

Готовый JAR:

```
java -jar vehicles.jar
```

Сборка и запуск одной командой (нужен JDK 17+):

- Windows: двойной клик по `build-and-run.bat` или `build-and-run.bat` в терминале;
- Linux / macOS / Git Bash: `./build-and-run.sh` (при необходимости `chmod +x build-and-run.sh`).

Скрипт компилирует исходники, собирает `vehicles.jar` и запускает его.

То же вручную:

```
javac --release 17 -encoding UTF-8 -d out src/*.java
jar cfe vehicles.jar Main -C out .
java -jar vehicles.jar
```

## Меню

| Пункт | Подпункты |
|-------|-----------|
| 1. Add | Manually — ввод вручную; From file — загрузка из файла |
| 2. Remove | By index — по номеру `#` из таблицы; By name — по имени (все совпадения, без учёта регистра) |
| 3. Sort | 3 шага: алгоритм → поле (Name / Speed / Year) → направление (Ascending / Descending) |
| 4. Filter | By name (includes); Speed: more than; Speed: less than; Reset filter |
| 5. Save | Сохранение всех объектов в файл (спрашивает подтверждение, если файл существует) |
| 6. Show names | Вывод только имён (текущий фильтр учитывается) |
| 0. Exit | Выход |

Если элементов больше 10, таблица разбита на страницы: `n` — следующая, `p` — предыдущая.
В любом запросе ввода пустая строка отменяет действие.

Алгоритмы сортировки: **Quick Sort**, **Bubble Sort**, **Insertion Sort**, **Stalin Sort** (собственные)
и **Stream API** (встроенный `sorted()`).

## Формат файла

Одна запись на строку: `name;speed;year`.
Пустые строки и строки, начинающиеся с `#`, игнорируются.
Некорректные строки пропускаются, а программа сообщает номер строки и причину.
Пример — `data/vehicles.txt`.

Ограничения валидации: имя 1–30 символов без `;`; скорость > 0 и ≤ 2000 км/ч;
год — 4 цифры, от 1886 до текущего.

## Структура проекта

| Файл | Назначение |
|------|------------|
| `Main` | точка входа |
| `VehicleApp` | контроллер: меню, действия, отображение |
| `Menu`, `Screen`, `ConsoleInput` | консольный интерфейс и ввод |
| `Vehicle`, `VehicleRow` | модель данных |
| `VehicleRepository` | хранение в `List<Vehicle>`, добавление, удаление |
| `VehicleParser`, `ValidationException` | разбор и валидация ввода |
| `VehicleFileService` | чтение (`try-with-resources`) и запись файлов |
| `VehicleFilter` | фильтры (`Predicate`) |
| `VehicleTableRenderer` | отрисовка таблицы |
| `SortAlgorithm<T>` | общий интерфейс сортировки (Generics) |
| `QuickSort`, `BubbleSort`, `InsertionSort`, `StalinSort`, `StreamSort` | реализации (полиморфизм) |
| `SortField`, `SortDirection` | поле и направление сортировки |
| `build-and-run.bat`, `build-and-run.sh` | сборка JAR и запуск |

Использование Stream API: фильтрация (`VehicleRepository.indicesMatching`, `VehicleApp.showNames`),
преобразование через `map()` (`VehicleApp.buildView`, `VehicleTableRenderer.render`, `VehicleApp.showNames`),
сортировка (`StreamSort`), постраничный вывод (`skip` / `limit`).
