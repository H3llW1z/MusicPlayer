# Тестовое задание для стажёра Android-направления (зимняя волна 2025)

## Инструкция по запуску:
* Работа велась в Android Studio Ladybug Feature Drop | 2024.2.2
* Используется AGP 8.8.0
* Приложение можно собрать как обычное одномодульное приложение. Сначала нажать "Sync Project with Gradle Files", затем выбрать в "Run/Debug Configurations" вариант "app" и нажать "Run app"

## Вопросы и проблемы:
- В задании не было указано, какую библиотеку использовать для воспроизведения музыки. Мной был выбран Exoplayer как официальный рекомендуемый способ воспроизведения контента. https://developer.android.com/media/media3/exoplayer
- Длительность трека из API приходит реальная, а для проигрывания доступны лишь 30-секундные отрезки. При преобразовании в сущности domain-слоя длительность высталяется равной 30 секундам.
- При пагинации по результатам поиска в API Deezer происходит пересечение элементов с предыдущей страницей, что может вызвать краш LazyColumn из-за повторяющихся ID. Решения данной проблемы, как и причины я не нашел. Чтобы обойти эту проблему, из начала новой страницы отбрасывается пересекающийся с предыдущей отрезок.

## Стек:
- Язык: **Kotlin**
- Многопоточность: **Coroutines, Flow**
- UI: **Jetpack Compose**
- Работа с сетью: **Retrofit**
- Инъекция зависимостей: **Dagger**
- Навигация: **Jetpack Navigation**
- Архитектура: **Clean Architecture, MVVM**
- Воспроизведение музыки: **Exoplayer**
- Загрузка изображений: **Coil**
- Сериализаторы: **Kotlinx Serialization, Gson**

## Реализовано:
- **Экран треков из API**
- **Экран воспроизведения треков**
- **Фоновый плеер** (есть функция перемещения между музыкальными композициями в фоновом режиме (через уведомление)
- **Навигация**

## Не реализовано:
- **Экран локальных треков, воспроизведение локальных треков**

## Известные проблемы и недочеты:
- Нарушение Clean Architecture, класс PlaybackService находится в presentation-слое и используется в реализации репозитория
- Должно быть информирование об ошибке сети на экране воспроизведения при проигрывании
- Скролл стопорится если пролистать до конца списка и не работает после подгрузки, если не отпустить экран и не начать снова
- При навигации назад на список треков NavigationBar появляется мгновенно и интерфейс плеера съезжает вверх
- При достижении конца плейлиста при проигрывании должны подгружаться новые треки и добавляться в плейлист
- В списке треков следует отображать текущий
- Текст в названиях и именах должен прокручиваться если не влезает в одну строку
- Скролл должен сбрасываться на начальное значение при смене чарт/поиск
- Реализовать экран воспроизведения следует в виде выезжающего снизу элемента, который может быть как свернут в узкую строку, так и развернут на весь экран
