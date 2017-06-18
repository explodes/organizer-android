package meta

import com.fernandocejas.arrow.optional.Optional


fun <T> Optional<T>.getOrNull(): T? {
    if (isPresent) {
        return get()
    } else {
        return null
    }
}
