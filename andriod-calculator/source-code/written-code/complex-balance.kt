package org.kotlinmath

import kotlin.math.*

/**
 * Exponential function
 */
var exp : (Complex) -> Complex = { z ->
    when (z) {
        NaN, INF -> NaN
        else -> {
            val r: Double = kotlin.math.exp(z.re)
            complex(r * kotlin.math.cos(z.im), r * kotlin.math.sin(z.im))
        }
    }
}

/**
 * Exponential function
 * @param z input
 * @return exp(z)
 */
fun exp(z: Number) = exp(complex(z.toDouble(), 0))

/**
 * Main branch of the Logarithmic function
 */
var ln :(Complex) -> Complex = { z ->
    when (z) {
        ZERO, INF, NaN -> NaN
        else -> complex(kotlin.math.ln(z.mod), atan2(z.im, z.re))
    }
}

/**
 * Logarithmic function
 * @param z input
 * @return ln(z)
 */
fun ln(z: Number) = ln(complex(z.toDouble(), 0))

/**
 * Sinus function
 */
var sin : (Complex) -> Complex = { z ->
    when (z) {
        NaN, INF -> NaN
        else -> {
            complex(
                    kotlin.math.sin(z.re) * kotlin.math.cosh(z.im),
                    kotlin.math.cos(z.re) * kotlin.math.sinh(z.im))
        }
    }
}

/**
 * Sinus function
 * @param z input
 * @return sin(z)
 */
fun sin(z: Number) = sin(complex(z.toDouble(), 0))

/**
 * Cosinus function
 */
var cos : (Complex) -> Complex = { z ->
    when (z) {
        NaN, INF -> NaN
        else -> {
            complex(
                    kotlin.math.cos(z.re) * kotlin.math.cosh(z.im),
                    -kotlin.math.sin(z.re) * kotlin.math.sinh(z.im))
        }
    }
}

/**
 * Cosinus function
 * @param z input
 * @return cos(z)
 */
fun cos(z: Number) = cos(complex(z.toDouble(), 0))


/**
 * Main branch of the Square Root function
 */
var sqrt : (Complex) -> Complex = { z ->
    when(z) {
        ZERO -> ZERO
        INF -> INF
        NaN -> NaN
        else -> {
            val t: Double = kotlin.math.sqrt((abs(z.re) + z.mod) / 2)
            if (z.re >= 0) {
                complex(t, z.im / (2 * t))
            } else {
                complex(kotlin.math.abs(z.im) / (2 * t), Math.copySign(1.0, z.im) * t)
            }
        }
    }
}

/**
 * Square Root function
 * @param z input
 * @return sqrt(z)
 */
fun sqrt(z: Number) = sqrt(complex(z.toDouble(), 0))

/**
 * Calculates the complex power. Please note, that similar to ln and sqrt the default
 * value is returned here.
 * @param z basis
 * @param w exponent
 * @return the power z^w
 */
var pow : (Complex, Complex) -> Complex = { z, w ->
    pow(z.mod, w) * exp(z.arg.I * w)
}

/**
 * The power function
 * @param x base
 * @param w exponent
 * @return x^w
 */
fun pow(x: Number, w:Complex): Complex {
    val d = x.toDouble()
    return when {
        d < 0.0 -> NaN
        d.isInfinite() -> NaN
        d.isNaN() -> NaN
        w.isInfinite() -> NaN
        w.isNaN() -> NaN
        w.isZero() -> ONE
        d == 0.0 -> ZERO
        else -> exp(kotlin.math.ln(d) * w)
    }
}

package org.kotlinmath

import java.util.*
import kotlin.math.*

/**
 * This interface represents a Complex number. Essentially, a complex number is
 * a tupel of two Double values, the real (re) and the imaginary (im) part.
 * Additionally, there are two further calculated properties arg and mod, which
 * are the values of the corresponding polar coordinate representation.
 * The main purpose of this interface/class is that you can combine complex numbers
 * using the four basic arithmetic operations (+, -, *, /) with each other but also
 * with all other objects of the type Number.
 */
interface Complex {

    companion object {
        const val DEFAULT_ZERO_SNAP_PRECISION = 1E-13
    }

    /** Real part */
    val re: Double

    /** Imaginary part */
    val im: Double

    // Hint: Usually it is not necessary to override the (calculated) properties
    // arg and mod with a lazy and caching-like kind. Using Kotlin's "lazy" increases
    // the execution time by a factor of 6. So the properties would have to be read
    // at least six times before caching would pay off, which is quite unlikely.

    /** The argument of this complex number (angle of the polar coordinate representation) */
    val arg: Double
        get() {
            return when {
                isInfinite() -> Double.NaN
                isNaN() -> Double.NaN
                re > 0.0 -> atan(im / re)
                re < 0.0 && im >= 0.0 -> atan(im / re) + PI
                re < 0.0 && im < 0.0 -> atan(im / re) - PI
                re == 0.0 && im > 0.0 -> PI / 2
                re == 0.0 && im < 0.0 -> -PI / 2
                else -> 0.0
            }
        }

    /** The modulus (absolute value) of this complex number (radius of the polar coordinate representation)  */
    val mod: Double
        get() {
            return kotlin.math.sqrt(re * re + im * im)
        }

    /**
     *  checks infinity property (remark that in case of complex numbers there is only one unsigned infinity)
     *  @return true if this is infinite
     */
    fun isInfinite() = this === INF

    /**
     * checks the "not a number" property (NaN represents an essential singularity)
     * @return true if this is NaN
     */
    fun isNaN() = this === NaN

    /**
     * checks to zero
     *  @return true if this is zero
     */
    fun isZero() = this == ZERO

    /**
     * Plus operator adding two complex numbers
     * @param z the summand
     * @return sum of this and z
     */
    operator fun plus(z: Complex): Complex {
        return when {
            isNaN() || z.isNaN() -> NaN
            isInfinite() -> if (z.isInfinite()) NaN else INF
            z.isInfinite() -> if (isInfinite()) NaN else INF
            else -> complex(re + z.re, im + z.im)
        }
    }

    /**
     * Plus operator adding a complex number and a number of type Double
     * @param x the summand
     * @return sum of this and x
     */
    operator fun plus(x: Double): Complex {
        return when {
            isNaN() || x.isNaN() -> NaN
            isInfinite() -> if (x.isInfinite()) NaN else INF
            x.isInfinite() -> if (isInfinite()) NaN else INF
            else -> complex(re + x, im)
        }
    }

    /**
     * Plus operator adding a complex number and one of type Number except Double
     * @param x the summand
     * @return sum of this and x
     */
    operator fun plus(x: Number) = plus(x.toDouble())

    /**
     * Minus operator subtracting two complex numbers
     * @param z the minuend
     * @return difference of this and x
     */
    operator fun minus(z: Complex): Complex {
        return when {
            isNaN() || z.isNaN() -> NaN
            isInfinite() -> if (z.isInfinite()) NaN else INF
            z.isInfinite() -> if (isInfinite()) NaN else INF
            else -> complex(re - z.re, im - z.im)
        }
    }

    /**
     * Minus operator subtracting a complex number and one of type Double
     * @param x the minuend
     * @return difference of this and x
     */
    operator fun minus(x: Double): Complex {
        return when {
            isNaN() || x.isNaN() -> NaN
            isInfinite() -> if (x.isInfinite()) NaN else INF
            x.isInfinite() -> if (isInfinite()) NaN else INF
            else -> complex(re - x, im)
        }
    }

    /**
     * Minus operator subtracting a complex number and one of type Number except Double
     * @param x the minuend
     * @return difference of this and x
     */
    operator fun minus(x: Number) = minus(x.toDouble())

    /**
     * Times operator multiplying two complex numbers
     * @param z the multiplicand
     * @return product of this and z
     */
    operator fun times(z: Complex): Complex {
        return when {
            isNaN() || z.isNaN() -> NaN
            isInfinite() -> if (z.isZero()) NaN else INF
            z.isInfinite() -> if (isZero()) NaN else INF
            else -> complex(re * z.re - im * z.im, im * z.re + re * z.im)
        }
    }

    /**
     * Times operator multiplying a complex number and one of type Double
     * @param x the multiplicand
     * @return product of this and x
     */
    operator fun times(x: Double): Complex {
        return when {
            isNaN() || x.isNaN() -> NaN
            isInfinite() -> if (x == 0.0) NaN else INF
            x.isInfinite() -> if (isZero()) NaN else INF
            else -> complex(re * x, im * x)
        }
    }

    /**
     * Times operator multiplying a complex number and one of type Number except Double
     * @param x the multiplicand
     * @return the product of this and x
     */
    operator fun times(x: Number) = times(x.toDouble())

    /**
     * Divide operator dividing two complex numbers
     * @param z the denominator
     * @return product of this and z
     */
    operator fun div(z: Complex): Complex {
        return when {
            isNaN() || z.isNaN() -> NaN
            isInfinite() -> if (z.isInfinite()) NaN else INF
            z.isInfinite() -> ZERO
            z.isZero() -> if (isZero()) NaN else INF
            else -> {
                val d = z.re * z.re + z.im * z.im
                complex((re * z.re + im * z.im) / d, (im * z.re - re * z.im) / d)
            }
        }
    }

    /**
     * Divide operator dividing a complex number and one of type Double
     * @param x the divisor
     * @return division of this and z
     */
    operator fun div(x: Double): Complex {
        return when {
            isNaN() || x.isNaN() -> NaN
            isInfinite() -> if (x.isInfinite()) NaN else INF
            x.isInfinite() -> ZERO
            x == 0.0 -> if (isZero()) NaN else INF
            else -> complex(re / x, im / x)
        }
    }

    /**
     * Divide operator dividing a complex number and one of type Number except Double
     * @param x the divisor
     * @return division of this and z
     */
    operator fun div(x: Number) = div(x.toDouble())

    /**
     * Negates a complex number
     * @return negation of this
     */
    operator fun unaryMinus(): Complex {
        return when {
            isNaN() -> NaN
            isInfinite() -> INF
            else -> complex(-re, -im)
        }
    }

    /**
     * Calculates the complex conjugation
     * @return complex conjugation of this
     */
    operator fun not(): Complex = conj()

    /**
     * Calculates the complex conjugation
     * @return complex conjugation of this
     */
    fun conj(): Complex {
        return when {
            isNaN() -> NaN
            isInfinite() -> INF
            else -> complex(re, -im)
        }
    }

    /**
     * Sets the real and/or the imaginary part to 0 if the value is lower than precision
     * @param precision
     * @return the "rounded" number
     */
    fun zeroSnap(precision: Double = DEFAULT_ZERO_SNAP_PRECISION): Complex {
        return complex(if (abs(re) <= precision) 0 else re,
                if (abs(im) <= precision) 0 else im
        )
    }

    /**
     * A string representation of a complex number (this) in the Form "2.5+3.1i" for example.
     * @param format This parameter affects the real an the imaginary part equally.
     * @param locale The locale determines e.g. whether a dot or a comma is output.
     */
    fun asString(format: String = "", locale: Locale = Locale.getDefault()): String {
        return when (this) {
            NaN -> "NaN"
            INF -> "Infinity"
            else -> {
                val reFormatted = if (format.isEmpty()) re.toString() else String.format(locale, format, re)
                val imFormatted = when (im) {
                    1.0 -> "i"
                    -1.0 -> "-i"
                    else -> "${if (format.isEmpty()) im.toString() else String.format(locale, format, im)}i"
                }
                if (re == 0.0) {
                    if (im == 0.0) "0.0" else imFormatted
                } else {
                    when {
                        im > 0.0 -> "$reFormatted+$imFormatted"
                        im < 0.0 -> "$reFormatted$imFormatted"
                        else -> reFormatted
                    }
                }
            }
        }
    }
}

/**
 * Makes a number "imaginary". The result is the same as if the number (this) is multiplied by I.
 * @return this * I
 */
val Number.I: Complex
    get() = complex(0, toDouble())

/**
 * Creates a complex number with this as real part and no imaginary part
 * @return this as complex number
 */
val Number.R: Complex
    get() = complex(toDouble(), 0)

/**
 * Plus operator adding a number of type Number and a complex one
 * @param z the summand
 * @return sum of this and z
 */
operator fun Number.plus(z: Complex) = z + this

/**
 * Minus operator subtracting a number of type Number and a complex one
 * @param z the minuend
 * @return difference of this and z
 */
operator fun Number.minus(z: Complex) = -z + this

/**
 * Times operator multiplying a number of type Number and a complex one
 * @param z the multiplicand
 * @return product of this and z
 */
operator fun Number.times(z: Complex) = z * this

/**
 * Division operator dividing a number of type Number and a complex one
 * @param z the divisor
 * @return division of this and z
 */
operator fun Number.div(z: Complex) = ONE / z * this

/**
 * Creates a complex number from a string. A valid representation is e.g. "2.5+3.1i"
 * @return the created complex number
 */
var toComplex: String.() -> Complex = {

    fun parseIm(arg: String): String {
        val im = arg.removeSuffix("i")
        return if (im.isEmpty()) "1.0" else im
    }

    when (this) {
        "Infinity" -> INF
        "NaN" -> NaN
        else -> {
            val parts = StringTokenizer(this, "+-", true)
                    .toList().map { it.toString().replace('I', 'i') }
            when (parts.size) {
                0 -> throw NumberFormatException("empty String")
                1 -> if (parts[0].endsWith("i")) {
                    complex(0.0, parseIm(parts[0]).toDouble())
                } else {
                    complex(parts[0].toDouble(), 0.0)
                }
                2 -> if (parts[1].endsWith("i")) {
                    complex(0.0, (parts[0] + parseIm(parts[1])).toDouble())
                } else {
                    complex((parts[0] + parts[1]).toDouble(), 0.0)
                }
                3 -> complex(parts[0].toDouble(), (parts[1] + parseIm(parts[2])).toDouble())
                4 -> complex((parts[0] + parts[1]).toDouble(), (parts[2] + parseIm(parts[3])).toDouble())
                else -> throw NumberFormatException("For input string: \"$this\"")
            }
        }
    }
}

/**
 * Creates a complex number from real and imaginary part.
 * Here instances of class <code>DefaultComplex</code> are created which implements 
 * the interface <code>Complex</code> is used by
 * the factory function <code>toComplex</code>. If you would like to use your own
 * implementation of <code>Complex</code> you can do this by replacing <code>toComplex</code>
 * with a factory which is creating your custom class. So, the entire application code can
 * remain the same.
 * @param re the real part
 * @param im the imaginary part
 * @return the created complex number
 */
var complex: (re: Number, im: Number) -> Complex = { re, im -> DefaultComplex(re.toDouble(), im.toDouble()) }

/** The imaginary unit i as constant */
val I = complex(0, 1)

/** Number 0 as complex constant */
val ZERO = complex(0, 0)

/** The real unit 1 as constant */
val ONE = complex(1, 0)

/** "Not a number" represents a essential singularity */
val NaN = complex(Double.NaN, Double.NaN)

/** Infinity represents the north pole of the complex sphere. */
val INF = complex(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)


/**
 * A default implementation of the interface <code>Complex</code>. This class is used by
 * the factory function <code>complex</code> (s. above). If you would like to use your own
 * implementation of <code>Complex</code> you can do this by replacing <code>complex</code>
 * with a factory function which is creating your custom class. In this way the entire
 * application code can remain the same.
 */
open class DefaultComplex(override val re: Double, override val im: Double = 0.0) : Complex {
    constructor(z: Complex) : this(z.re, z.im)
    constructor(str: String) : this(str.toComplex())

    // equals had to be overwritten because of a bug comparing data classes with
    // -0.0 as (real or imaginary) Double value. Without overwriting equals the
    // following would apply: DefaultComplex(0.0, 0.0) != DefaultComplex(-0.0, 0.0)
    // although 0.0 == -0.0.

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other === null) return false
        if (other is Complex) {
            if (re != other.re) return false
            if (im != other.im) return false
            return true
        }
        return false
    }

    override fun hashCode(): Int {
        var result = re.hashCode()
        result = 31 * result + im.hashCode()
        return result
    }

    override fun toString() = asString()
}