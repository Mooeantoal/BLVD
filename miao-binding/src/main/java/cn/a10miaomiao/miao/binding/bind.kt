package cn.a10miaomiao.miao.binding

object Bind {
    var binding: MiaoBinding? = null
}

typealias ViewBindingFn<T, V> = V.(T) -> Unit

inline fun <T> miaoRef(initialTarget: T): MiaoBinding.RefData<T> {
    return Bind.binding?.let {
        it.persist()
        it.next(Unit, MiaoBinding.RefData(initialTarget))
        TypeSafe.safeCast<MiaoBinding.RefData<T>>(it.cur().target, MiaoBinding.RefData(initialTarget))
    } ?: throw IllegalStateException("Binding is not initialized. Please ensure MiaoBinding is properly set up.")
}

inline fun <T, V> V.miaoEffect(value: T, viewBinding: ViewBindingFn<T, V>) {
    val realTarget = Bind.binding?.next(value, this)
    if (realTarget != null) {
        viewBinding(realTarget, value)
    }
}

inline fun <T, V> V.miaoEffect(value: T, viewBinding: ViewBindingFn<T, V>, viewUpdate: ViewBindingFn<T,V>) {
    val realTarget = Bind.binding?.next(value, this)
    if (realTarget != null) {
        viewBinding(realTarget, value)
    } else {
        Bind.binding?.cur()?.let {
            val target = TypeSafe.safeCast<V>(it.target, this)
            viewUpdate(target, value)
        }
    }
}

inline fun <T, V> miaoMemo(value: V, initialMemo: (V) -> T): T {
    val ref = miaoRef<T?>(null)
    ref.miaoEffect(value) {
        ref.value = initialMemo(value)
    }
    return TypeSafe.safeCast<T>(ref.value) { e ->
        throw IllegalStateException("miaoMemo initialization failed: ${e.message}", e)
    } ?: initialMemo(value)
}
