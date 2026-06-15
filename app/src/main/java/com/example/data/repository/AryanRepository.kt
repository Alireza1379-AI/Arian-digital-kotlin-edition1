package com.example.data.repository

import com.example.data.database.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AryanRepository(private val dao: AryanDao) {

    val allServices: Flow<List<ServiceEntity>> = dao.getAllServices()
    val allOrders: Flow<List<OrderEntity>> = dao.getAllOrders()
    val allTransactions: Flow<List<WalletTransaction>> = dao.getAllTransactions()
    val userProfile: Flow<UserProfile?> = dao.getUserProfileFlow()

    suspend fun initializeDatabaseIfEmpty() = withContext(Dispatchers.IO) {
        // Pre-populate Profile
        val existingProfile = dao.getUserProfile()
        if (existingProfile == null) {
            dao.insertUserProfile(
                UserProfile(
                    id = 1,
                    name = "سید آرین محمدی",
                    role = "CUSTOMER", // Default role
                    phoneNumber = "09123456789",
                    balance = 850000 // 850,000 Tomans
                )
            )
            // Add some base transactions
            dao.insertTransaction(WalletTransaction(amount = 1000000, type = "DEPOSIT", description = "افزایش اعتبار اولیه حساب"))
            dao.insertTransaction(WalletTransaction(amount = -150000, type = "PAYMENT", description = "پرداخت هزینه سفارش پیش‌فرض"))
        }

        // Pre-populate Services
        val services = dao.getAllServices().first()
        if (services.isEmpty()) {
            val baseServices = listOf(
                ServiceEntity(
                    id = 1,
                    title = "پرداخت هزینه‌های آنلاین",
                    category = "خدمات پرداخت",
                    basePrice = 15000,
                    unitType = "واحد",
                    description = "پرداخت هرگونه هزینه ثبت‌نام دانشگاهی، آزمون سفارت، اشتراک سایت‌های معتبر خارجی، سفارش یا خرید کالا",
                    limitInfo = "مبلغ نهایی معادل اصل صورت‌حساب ارزی به علاوه کارمزد ثابت تراکنش است."
                ),
                ServiceEntity(
                    id = 2,
                    title = "تایپ ساده فوری",
                    category = "خدمات اداری",
                    basePrice = 8000,
                    unitType = "صفحه",
                    description = "تایپ فایل‌های صوتی، دست‌نویس یا تصاویر کتاب‌ها به‌صورت خوانا و در قالب استاندارد Word",
                    limitInfo = "تایپ ساده روان، حداقل ۱۸ خط در هر صفحه مجزا با فونت سایز ۱۶ نازنین."
                ),
                ServiceEntity(
                    id = 3,
                    title = "ایجاد ایمیل معتبر توسط اپراتور",
                    category = "خدمات اداری",
                    basePrice = 12000,
                    unitType = "واحد",
                    description = "ساخت جیمیل یا ایمیل بین‌المللی همراه با تنظیم سوالات امنیتی، ایمیل پشتیبان و شماره احراز هویت متصل به پنل مستقل",
                    limitInfo = "تحویل کامل اطلاعات ورود، کلیدهای بازیابی دو مرحله‌ای به همراه تعهد عدم دسترسی ثانویه اپراتور."
                ),
                ServiceEntity(
                    id = 4,
                    title = "طراحی هر صفحه پاورپوینت",
                    category = "خدمات اداری",
                    basePrice = 10000,
                    unitType = "صفحه",
                    description = "تهیه اسلایدهای جذاب، اینفوگرافیک و انیمیشن‌های حرفه‌ای مناسب کنفرانس، سمینارهای علمی یا تجاری و پایان‌نامه‌ها",
                    limitInfo = "طراحی و چیدمان مطالب ارائه‌شده توسط خریدار بدون نیاز به فیش‌برداری متون."
                ),
                ServiceEntity(
                    id = 5,
                    title = "طراحی نشانه (لوگو) - تک اتود",
                    category = "طراحی گرافیک",
                    basePrice = 180000,
                    unitType = "نمونه",
                    description = "خلق هویت بصری مدرن و لوگوی مینیمال طبق اصول استاندارد طراحی نشانه زیر نظر طراح فریلنسر ارشد",
                    limitInfo = "ارائه یک اتود کانسپت اصلی با کیفیت و رزولوشن بالا به همراه ۲ بار ویرایش فرمی یا رنگی کوچک."
                ),
                ServiceEntity(
                    id = 6,
                    title = "بازسازی یا ویرایش لوگو قدیمی",
                    category = "طراحی گرافیک",
                    basePrice = 95000,
                    unitType = "نمونه",
                    description = "وکتورایز کردن تصاویر لوگوی پیکسلی کیفیت پایین و خروجی باکیفیت و بدون افت کیفیت با فرمت SVG/AI",
                    limitInfo = "نیازمند بارگذاری بارزترین تصویر نمونه در دسترس از لوگو فعلی توسط مشتری."
                ),
                ServiceEntity(
                    id = 7,
                    title = "بروشور تک‌برگ / تراکت / فلایر",
                    category = "طراحی گرافیک",
                    basePrice = 75000,
                    unitType = "نمونه",
                    description = "طراحی کامل برگه تبلیغاتی پرمخاطب مناسب چاپخانه‌های افست و دیجیتال یا توزیع در رسانه‌های اجتماعی کلاینت",
                    limitInfo = "تحویل در ۲ مود رنگی استاندارد RGB و CMYK با رزولوشن تحویلی ۳۰۰ DPI."
                ),
                ServiceEntity(
                    id = 8,
                    title = "طراحی صفحات داخلی بروشور",
                    category = "طراحی گرافیک",
                    basePrice = 35000,
                    unitType = "صفحه",
                    description = "صفحه‌آرایی، ستون‌بندی و گریدبندی حرفه‌ای بروشورهای چندلت و چندصفحه‌ای برای محصولات و ارائه‌ها",
                    limitInfo = "قیمت بر اساس تعداد صفحات داخلی است و حداکثر سقف سفارش تا ۸ صفحه تعریف شده است."
                ),
                ServiceEntity(
                    id = 9,
                    title = "طراحی صفحات داخلی کاتالوگ",
                    category = "طراحی گرافیک",
                    basePrice = 40000,
                    unitType = "صفحه",
                    description = "تنظیم چیدمان عکس‌های محصولات، کدهای فنی کالا و جدول مشخصات به صورت طبقه‌بندی شده و شیک",
                    limitInfo = "حداکثر سقف کارکرد برای کالیبره‌سازی صفحات تا ۱۶ صفحه داخلی مجزا می‌باشد."
                ),
                ServiceEntity(
                    id = 10,
                    title = "طراحی پست و استوری شبکه اجتماعی",
                    category = "طراحی گرافیک",
                    basePrice = 50000,
                    unitType = "نمونه",
                    description = "تولید تصاویر مدرن و بنرهای مناسب استوری یا پورتفولیو اینستاگرام برای جذب نرخ تعامل بالای مخاطبان هدف",
                    limitInfo = "حداکثر تا ۵ نمونه گرافیک مجزا در یک سبد سفارش قابل ثبت و ارائه است."
                ),
                ServiceEntity(
                    id = 11,
                    title = "ساخت اینفوگرافیک اداری A4",
                    category = "طراحی گرافیک",
                    basePrice = 120000,
                    unitType = "نمونه",
                    description = "تبدیل حجم وسیعی از گزارش‌های سالانه مالی یا عملکرد سازمانی کلاینت به الگوهای مقایسه‌ای ساده تصویری در ابعاد A4",
                    limitInfo = "تحویل خروجی نهایی با فرمت‌های PNG شفاف و PDF باکیفیت جهت استفاده در وب."
                ),
                ServiceEntity(
                    id = 12,
                    title = "طراحی رابط کاربری اپلیکیشن (Figma + AI)",
                    category = "طراحی گرافیک",
                    basePrice = 250000,
                    unitType = "صفحه",
                    description = "طراحی پکیج کامل سیمانی، وایرفریم‌ها و صفحات پوسته نهایی در ابزار فیگما با اهرم کردن هوش مصنوعی جهت سرعت‌بخشی",
                    limitInfo = "تحویل لینک اشتراک‌گذاری پروژه فعال Figma با دسترسی کپی پالت رنگ‌ها و کامپوننت‌ها."
                ),
                // Gift Cards
                ServiceEntity(id = 13, title = "خرید گیفت کارت Apple ID", category = "گیفت کارت", basePrice = 15000, unitType = "هر $10 ارزش", description = "خرید اورجینال گیفت‌کارت اپل", limitInfo = "اجرت ثبت به ازای هر ۱۰ دلار است. شارژ حساب با کدهای ۱۶ رقمی معتبر."),
                ServiceEntity(id = 14, title = "خرید گیفت کارت Amazon", category = "گیفت کارت", basePrice = 12000, unitType = "هر $10 ارزش", description = "کد اورجینال آمازون جهت خرید کالا و خدمات آنلاین مارکت خارجی", limitInfo = "هر ۱۰ دلار ارزش گیفت کارت به کارمزد ثابت اضافه می‌شود."),
                ServiceEntity(id = 15, title = "خرید گیفت کارت PlayStation", category = "گیفت کارت", basePrice = 14000, unitType = "هر $10 ارزش", description = "اعتبار مستقیم پلی‌استیشن استور جهت خرید بازی‌های دیجیتالی و تمدید پلاس", limitInfo = "ریجن اکانت حتما هماهنگ با ریجن خرید گیفت کارت باشد."),
                ServiceEntity(id = 16, title = "اجرت خرید گیفت کارت Xbox", category = "گیفت کارت", basePrice = 13000, unitType = "هر $10 ارزش", description = "خدمات واسط خرید و صدور سریع گیفت‌کارت مایکروسافت/ایکس‌باکس", limitInfo = "اعمال بر اساس ضرایب فاکتورهای ۱۰ دلاری خرید معتبر."),
                ServiceEntity(id = 17, title = "اجرت خرید گیفت کارت Steam", category = "گیفت کارت", basePrice = 16000, unitType = "هر $10 ارزش", description = "شارژ امن حساب پلتفرم استیم برای خرید مستقیم بازی‌های نسخه کلاینت کامپیوتر", limitInfo = "ثبت کامپوننت سیستمی بلافاصله بعد از نهایی شدن پرداخت."),
                ServiceEntity(id = 18, title = "اجرت خرید گیفت کارت Google Play", category = "گیفت کارت", basePrice = 14000, unitType = "هر $10 ارزش", description = "کارمزد شارژ اکانت گوگل‌پلی برای خریدهای مارکت اندروید", limitInfo = "محدودیت‌های مربوط به تحریم‌ها و ریجن آمریکا الزامی است."),
                ServiceEntity(id = 19, title = "اجرت خرید گیفت کارت Netflix", category = "گیفت کارت", basePrice = 15000, unitType = "هر $10 ارزش", description = "تمدید و خرید مستقیم اشتراک سرویس استریم آنلاین نتفلیکس بدون نیاز به مسترکارت شخصی", limitInfo = "تحویل کد ردیم امن نتفلیکس با ریجن هماهنگ.")
            )
            dao.insertServices(baseServices)
        }

        // Check and pre-populate an initial/default order so tracking screen has content on launch
        val orders = dao.getAllOrders().first()
        if (orders.isEmpty()) {
            val defaultOrderId = dao.insertOrder(
                OrderEntity(
                    serviceId = 2,
                    serviceTitle = "تایپ ساده فوری",
                    category = "خدمات اداری",
                    customerName = "سید آرین محمدی",
                    operatorName = "زهرا رضایی (اپراتور ارشد)",
                    currentStatus = "InProgress",
                    totalPrice = 120000,
                    qty = 15,
                    notes = "تایپ پایان‌نامه زبان انگلیسی با رعایت علائم نگارشی"
                )
            ).toInt()

            // Pre-populate some messages for this order
            dao.insertMessage(SupportMessage(orderId = defaultOrderId, senderName = "زهرا رضایی", senderRole = "OPERATOR", text = "سلام آرین عزیز، فایل شما دریافت شد و در حال تایپ است. آیا در قالب فایل Word ارسال شود؟"))
            dao.insertMessage(SupportMessage(orderId = defaultOrderId, senderName = "سید آرین محمدی", senderRole = "CUSTOMER", text = "سلام، بله لطفاً با نسخه docx ذخیره و ارسال فرمایند. ممنون از سرعت عمل شما."))
        }
    }

    suspend fun getServiceById(id: Int): ServiceEntity? {
        return dao.getServiceById(id)
    }

    suspend fun insertOrder(order: OrderEntity): Long {
        return dao.insertOrder(order)
    }

    suspend fun updateOrder(order: OrderEntity) {
        dao.updateOrder(order)
    }

    suspend fun updateOrderStatus(id: Int, status: String) {
        dao.updateOrderStatus(id, status)
    }

    suspend fun submitRating(orderId: Int, rating: Int, comment: String) {
        val order = dao.getOrderById(orderId)
        if (order != null) {
            dao.updateOrder(order.copy(rating = rating, ratingComment = comment))
        }
    }

    fun getOrderByIdFlow(id: Int): Flow<OrderEntity?> = dao.getOrderByIdFlow(id)

    suspend fun depositWallet(amount: Long) {
        val profile = dao.getUserProfile() ?: return
        val newBalance = profile.balance + amount
        dao.updateWalletBalance(newBalance)
        dao.insertTransaction(
            WalletTransaction(
                amount = amount,
                type = "DEPOSIT",
                description = "افزایش موجودی کیف پول آنلاین"
            )
        )
    }

    suspend fun payOrder(orderId: Int): Boolean {
        val order = dao.getOrderById(orderId) ?: return false
        val profile = dao.getUserProfile() ?: return false
        if (profile.balance >= order.totalPrice && order.currentStatus == "PendingPayment") {
            val newBalance = profile.balance - order.totalPrice
            dao.updateWalletBalance(newBalance)
            dao.updateOrderStatus(orderId, "Paid")
            dao.insertTransaction(
                WalletTransaction(
                    amount = -order.totalPrice,
                    type = "PAYMENT",
                    description = "پرداخت هزینه سفارش ${order.serviceTitle} (#$orderId)"
                )
            )
            return true
        }
        return false
    }

    suspend fun refundOrder(orderId: Int): Boolean {
        val order = dao.getOrderById(orderId) ?: return false
        val profile = dao.getUserProfile() ?: return false
        val refundStatuses = listOf("Paid", "Assigned", "InProgress")
        if (refundStatuses.contains(order.currentStatus)) {
            val newBalance = profile.balance + order.totalPrice
            dao.updateWalletBalance(newBalance)
            dao.updateOrderStatus(orderId, "Refunded")
            dao.insertTransaction(
                WalletTransaction(
                    amount = order.totalPrice,
                    type = "REFUND",
                    description = "استرداد وجه سفارش ${order.serviceTitle} (#$orderId)"
                )
            )
            return true
        }
        return false
    }

    fun getMessagesForOrder(orderId: Int): Flow<List<SupportMessage>> = dao.getMessagesForOrder(orderId)

    suspend fun sendMessage(orderId: Int, senderName: String, senderRole: String, text: String) {
        dao.insertMessage(SupportMessage(orderId = orderId, senderName = senderName, senderRole = senderRole, text = text))
    }

    suspend fun updateUserRole(newRole: String) {
        dao.updateUserRole(newRole)
    }

    suspend fun updateServicePrice(id: Int, newPrice: Long) {
        dao.updateServicePrice(id, newPrice)
    }
}
