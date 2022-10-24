У нас в Константе несколько проектов с большим набором функций, часть из которых присутствует во всех (или, по
крайней мере, во многих) разделах интерфейса приложения. Такими функциями может быть авторизация(имеется в виду
регистрация+вход), добавление товаров в корзину,
информация о балансе пользователя, уведомления о новых входящих или что-то другое.

Эта статья ориентирована на начинающих разработчиков, которые уже познакомились с базовым принципами
объектно-ориентированного программирования, такими как абстракция, инкапсуляция, наследование и полиморфизм, а также
владеют
основными библиотеками и инструментами, актуальными для современной Android-разработки (Android Navigation Components,
Hilt, RecyclerView)
Цель - показать, что существуют другие возможные приёмы и паттерны, а также, что любая задача может иметь несколько
решений.
Пример будет основан на паттерне MVVM и достаточно упрощен, чтобы сконцентрироваться на организации кода, связанного со
сквозной логикой приложения. В частности, RecyclerView заменён на ScrollView+Linearlayout, все интеректоры являются
моками намерено.

Давайте представим что нам нужно отображать несколько экранов, таких как каталог товаров, детальную информацию по
товару, новости, акции и возможно что-то еще. На каждом из этих экранов по задумке дизайнеров должна быть доступна
корзина. Для начала добавим эту функциональность на экран каталога товаров:

```kotlin
@AndroidEntryPoint
class CatalogListFragment : Fragment(R.layout.fragment_catalog_list) {

    val viewModel by viewModels<CatalogListViewModel>()

    private var catalogContainer: LinearLayout? = null
    private var cartItemsCount: TextView? = null
    private var cartFab: FloatingActionButton? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        catalogContainer = view.findViewById(R.id.catalog_container)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.catalogItems.collect { items ->
                showCatalogItems(items)
            }
        }

        cartItemsCount = view.findViewById(R.id.cart_items_count)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cartItemsCount.collect {
                cartItemsCount?.text = it.toString()
            }
        }

        cartFab = view.findViewById<FloatingActionButton?>(R.id.cart_fab)?.apply {
            setOnClickListener {
                showCartDialog()
            }
        }
    }

    private fun showCatalogItems(items: List<CatalogItem>) {
        // ...
    }

    private fun showCartDialog() {
        // ...
    }

}
```

```kotlin
@HiltViewModel
class CatalogListViewModel @Inject constructor(
    private val catalogInteractor: CatalogInteractor,
    private val cartInteractor: CartInteractor,
) : ViewModel() {

    val _catalogItems: MutableStateFlow<List<CatalogItem>> = MutableStateFlow(emptyList())
    val catalogItems: Flow<List<CatalogItem>> = _catalogItems
    val cartItems: Flow<List<CartItem>> = cartInteractor.cartItems
    val cartItemsCount: Flow<Int> = cartInteractor.totalItemsCount

    init {
        viewModelScope.launch {
            _catalogItems.emit(
                catalogInteractor.getCatalogItems()
            )
        }
    }

    fun addToCart(item: CatalogItem) {
        viewModelScope.launch {
            cartInteractor.addCatalogItem(item)
        }
    }

}
```

При добавлении этой функциональности на экран детальной информации по товару из каталога очень быстро станет понятно,
что мы столкнёмся с дублированием кода. Все прекрасно понимают, что дублирование кода - это плохо, и как раз
наследование способно решить эту поблему.
Первое, что приходит в голову - создать базовый класс вьюмодели, в котором можно описать общую для этих двух экранов
логику:

```kotlin
abstract class BaseCartViewModel(
    private val cartInteractor: CartInteractor,
) : ViewModel() {

    val cartItems: Flow<List<CartItem>> = cartInteractor.cartItems
    val cartItemsCount: Flow<Int> = cartInteractor.totalItemsCount

    fun addToCart(item: CatalogItem) {
        viewModelScope.launch {
            cartInteractor.addCatalogItem(item)
        }
    }

    fun removeCartItem(item: CartItem) {
        viewModelScope.launch {
            cartInteractor.removeCartItem(item)
        }
    }
}
```

Сделав такую заготовку, мы действительно избавимся от дублирования кода во вьюмоделях и упростим добавление
функциональности корзины на новые экраны

```kotlin
@HiltViewModel
class CatalogListViewModel @Inject constructor(
    private val catalogInteractor: CatalogInteractor,
    cartInteractor: CartInteractor,
) : BaseCartViewModel(cartInteractor) {

    val _catalogItems: MutableStateFlow<List<CatalogItem>> = MutableStateFlow(emptyList())
    val catalogItems: Flow<List<CatalogItem>> = _catalogItems

    init {
        viewModelScope.launch {
            _catalogItems.emit(
                catalogInteractor.getCatalogItems()
            )
        }
    }

}
```

```kotlin
@HiltViewModel
class CatalogDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val catalogInteractor: CatalogInteractor,
    cartInteractor: CartInteractor,
) : BaseCartViewModel(cartInteractor) {

    private var catalogItem: CatalogItem? = null
    private val _itemInfo: MutableStateFlow<String> = MutableStateFlow("")
    val itemInfo: Flow<String> = _itemInfo

    init {
        // ...
    }

    fun addToCart() {
        catalogItem?.also {
            addToCart(it)
        }
    }

}
```

Всё выглядит прекрасно, но у нас осталось дублирование кода в слое представления (во фрагментах). Почему бы не пойти тем
же путём и сделать базовый фрагмент

```kotlin
abstract class BaseCartFragment(
    @LayoutRes contentLayoutId: Int
) : Fragment(contentLayoutId) {

    abstract val vm: BaseCartViewModel

    private var cartItemsCount: TextView? = null
    private var cartFab: FloatingActionButton? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cartItemsCount = view.findViewById(R.id.cart_items_count)
        viewLifecycleOwner.lifecycleScope.launch {
            vm.cartItemsCount.collect {
                cartItemsCount?.text = it.toString()
            }
        }

        cartFab = view.findViewById<FloatingActionButton?>(R.id.cart_fab)?.apply {
            setOnClickListener {
                showCartDialog()
            }
        }
    }

    private fun showCartDialog() {
        // ...
    }

}
```

```kotlin
@AndroidEntryPoint
class CatalogListFragment : BaseCartFragment(R.layout.fragment_catalog_list) {

    val viewModel by viewModels<CatalogListViewModel>()

    override val vm: BaseCartViewModel
        get() {
            return viewModel
        }

    private var catalogContainer: LinearLayout? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        catalogContainer = view.findViewById(R.id.catalog_container)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.catalogItems.collect { items ->
                showCatalogItems(items)
            }
        }
    }

    private fun showCatalogItems(items: List<CatalogItem>) {
        // ...
    }

}
```

```kotlin
@AndroidEntryPoint
class CatalogDetailsFragment : BaseCartFragment(R.layout.fragment_catalog_details) {

    val viewModel by viewModels<CatalogDetailsViewModel>()

    override val vm: BaseCartViewModel
        get() {
            return viewModel
        }

    private var text: TextView? = null
    private var addToCart: ImageView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        text = view.findViewById(R.id.text)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.itemInfo.collect {
                text?.text = it
            }
        }

        addToCart = view.findViewById<ImageView?>(R.id.add_to_cart)?.apply {
            setOnClickListener {
                viewModel.addToCart()
            }
        }
    }

}
```

В общем и целом всё работает, но в данной реализации есть некоторые проблемы. Базовый фрагмент надеется, что наследники
будут иметь в верстке FloatingActionButton, при чем именно с идентификатором bucket_fab, иначе всё молча перестанет
работать, но это не всё.

Теперь давайте представим что продуктологи/дизайнеры заказчики решили добавить кнопки входа в случае если пользователь
не авторизован на все ключевые экраны. Следуя нашей прошлой логике нужно делать базовые абстрактные
BaseAuthControlsFragment и BaseAuthControlsViewModel.

```kotlin
abstract class BaseAuthControlsFragment(
    @LayoutRes contentLayoutId: Int
) : Fragment(contentLayoutId) {

    abstract val vm: BaseAuthControlsViewModel

    private var authControlsContainer: LinearLayout? = null
    private var signUp: Button? = null
    private var signIn: Button? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authControlsContainer = view.findViewById(R.id.auth_controls_container)
        signUp = view.findViewById<Button?>(R.id.sign_up)?.apply {
            setOnClickListener {
                vm.onSignUpClick()
            }
        }
        signIn = view.findViewById<Button?>(R.id.sign_in)?.apply {
            setOnClickListener {
                vm.onSignInClick()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            vm.authControlsState.collect {
                when (it) {
                    AuthControlsState.AVAILABLE -> authControlsContainer?.visibility == View.VISIBLE
                    AuthControlsState.UNAVAILABLE -> authControlsContainer?.visibility == View.GONE
                }
            }
        }
    }
}
```

```kotlin
abstract class BaseAuthControlsViewModel(
    private val authInteractor: AuthInteractor
) : ViewModel() {

    val authControlsState: Flow<AuthControlsState> = authInteractor.authState.map {
        when (it) {
            AuthState.AUTHORIZED -> AuthControlsState.UNAVAILABLE
            AuthState.UNAUTHORIZED -> AuthControlsState.AVAILABLE
        }
    }

    fun onSignUpClick() {
        viewModelScope.launch {
            authInteractor.auth()
        }
    }

    fun onSignInClick() {
        viewModelScope.launch {
            authInteractor.auth()
        }
    }

}
```

Теперь нужно унаследовать эти классы на тех экранах, где нам нужны эти кнопки. К сожалению в котлине (как в и Java)
недоступно множественное наследование и придётся делать каскадное наследование, чтобы получить обе функциональности на
одном экране. Абсолютно непонятно какой из этих фрагментов/вьюмоделей должен быть выше в иерархии наследования. Как
всегда вопросов больше, чем ответов

К счастью в ООП есть другие механизмы для построения классов и связей между ними. На ряду с наследованием
существует [ассоциация](https://ru.wikipedia.org/wiki/%D0%90%D1%81%D1%81%D0%BE%D1%86%D0%B8%D0%B0%D1%86%D0%B8%D1%8F_(%D0%BE%D0%B1%D1%8A%D0%B5%D0%BA%D1%82%D0%BD%D0%BE-%D0%BE%D1%80%D0%B8%D0%B5%D0%BD%D1%82%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%BD%D0%BE%D0%B5_%D0%BF%D1%80%D0%BE%D0%B3%D1%80%D0%B0%D0%BC%D0%BC%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5))
.
Она в свою очередь бывает двух видов:

- Композиция - вариант ассоциации, при котором часть целого не может существовать вне главного объекта (объект А
  полностью управляет временем жизни объекта B)

```kotlin
class A {
    private val b = B()
}
```

- Агрегация - часть целого имеет своё время жизни, объект A получает ссылку на объект B извне и использует его

```kotlin
class A(
    private val b: B
) {

}
```

Попробуем применить агрегацию для совместного использования BaseCartViewModel и BaseAuthControlsViewModel

```kotlin
@HiltViewModel
class CatalogListViewModel @Inject constructor(
    private val catalogInteractor: CatalogInteractor,
    private val cartViewModel: BaseCartViewModel,
    private val authControlsViewModel: BaseAuthControlsViewModel,
) : ViewModel() {

    val _catalogItems: MutableStateFlow<List<CatalogItem>> = MutableStateFlow(emptyList())
    val catalogItems: Flow<List<CatalogItem>> = _catalogItems

    val cartItems: Flow<List<CartItem>> = cartViewModel.cartItems
    val cartItemsCount: Flow<Int> = cartViewModel.cartItemsCount
    val authControlsState: Flow<AuthControlsState> = authControlsViewModel.authControlsState

    init {
        viewModelScope.launch {
            _catalogItems.emit(
                catalogInteractor.getCatalogItems()
            )
        }
    }

    fun addToCart(item: CatalogItem) {
        cartViewModel.addToCart(item)
    }

    fun removeCartItem(item: CartItem) {
        cartViewModel.removeCartItem(item)
    }

    fun onSignUpClick() {
        authControlsViewModel.onSignUpClick()
    }

    fun onSignInClick() {
        authControlsViewModel.onSignUpClick()
    }

}
```

У нас получилось использовать корзину и авторизацию в рамках одной вьюмодели, но всё равно много болейрплейт кода.
Вспоминаем что создатели языка котлин уже решили эту проблему добавив
[делегаты](https://kotlinlang.org/docs/delegation.html) в язык
Определим интерфейсы этих двух функциональностей

```kotlin
interface CartVMDelegate {
    val cartItems: Flow<List<CartItem>>
    val cartItemsCount: Flow<Int>

    fun addToCart(item: CatalogItem)
    fun removeCartItem(item: CartItem)
}
interface AuthControlsVMDelegate {

    val authControlsState: Flow<AuthControlsState>

    fun onSignUpClick()
    fun onSignInClick()
}
```

Теперь вьюмодели наших экранов могут стать более чистыми

```kotlin
@HiltViewModel
class CatalogListViewModel @Inject constructor(
    private val catalogInteractor: CatalogInteractor,
    private val cartVMDelegate: CartVMDelegate,
    private val authControlsVMDelegate: AuthControlsVMDelegate,
) : ViewModel(),
    AuthControlsVMDelegate by authControlsVMDelegate,
    CartVMDelegate by cartVMDelegate {

    val _catalogItems: MutableStateFlow<List<CatalogItem>> = MutableStateFlow(emptyList())
    val catalogItems: Flow<List<CatalogItem>> = _catalogItems

    init {
        viewModelScope.launch {
            _catalogItems.emit(
                catalogInteractor.getCatalogItems()
            )
        }
    }

}
```

При этом мы всегда можем переопределить любой метод и добавить что (логи, аналитику, etc) если это понадобится

```kotlin
override fun onSignInClick() {
    analytics.logEvent(/**/)
    authControlsVMDelegate.onSignInClick()
}
```

Кажется все проблемы во вьюмоделях решены. Вернёмся к фрагментам: отдельные куски кода, обрабатывающие данные от
делегатов вьюмоделей тоже можно вынести в отдельные классы и подключать с помощью агрегации в нужные фрагменты

```kotlin
class AuthControlsViewDelegate {

    private var authControlsContainer: LinearLayout? = null
    private var signUp: Button? = null
    private var signIn: Button? = null

    fun setUp(
        viewLifecycleOwner: LifecycleOwner,
        authControlsContainer: LinearLayout,
        viewModel: AuthControlsVMDelegate
    ) {
        this.authControlsContainer = authControlsContainer
        signUp = authControlsContainer.findViewById<Button?>(R.id.sign_up)?.apply {
            setOnClickListener {
                viewModel.onSignUpClick()
            }
        }
        signIn = authControlsContainer.findViewById<Button?>(R.id.sign_in)?.apply {
            setOnClickListener {
                viewModel.onSignInClick()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.authControlsState.collect {
                when (it) {
                    AuthControlsState.AVAILABLE -> authControlsContainer.visibility = View.VISIBLE
                    AuthControlsState.UNAVAILABLE -> authControlsContainer.visibility = View.GONE
                }
            }
        }
    }

}
```

```kotlin
@AndroidEntryPoint
class CatalogDetailsFragment : Fragment(R.layout.fragment_catalog_details) {

    val viewModel by viewModels<CatalogDetailsViewModel>()

    private var text: TextView? = null
    private var addToCart: ImageView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // ...
        AuthControlsViewDelegate().setUp(
            viewLifecycleOwner = viewLifecycleOwner,
            authControlsContainer = view.findViewById(R.id.auth_controls_container),
            viewModel = viewModel
        )
        // ...
    }

}
```



