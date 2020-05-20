package club.sk1er.vigilance.oldgui

import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.components.UIText
import club.sk1er.elementa.components.UIWrappedText
import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import club.sk1er.vigilance.data.PropertyData
import club.sk1er.vigilance.gui.components.Slider
import net.minecraft.client.Minecraft
import org.lwjgl.input.Keyboard
import java.awt.Color

class SliderSetting(name: String, description: String, private val prop: PropertyData) : SettingObject(prop) {
    private val drawBox = UIBlock().constrain {
        height = ChildBasedSizeConstraint() + 15.pixels()
        width = RelativeConstraint()
        color = Color(0, 0, 0, 0).asConstraint()
    } childOf this

    private val title = UIText(name).constrain {
        x = 3.pixels()
        y = 3.pixels()
        width = PixelConstraint(Minecraft.getMinecraft().fontRendererObj.getStringWidth(name) * 2f)
        height = 18.pixels()
        color = Color(255, 255, 255, 10).asConstraint()
    } childOf drawBox

    private val text = UIWrappedText(description).constrain {
        x = 3.pixels()
        y = 25.pixels()
        width = FillConstraint() - 50.pixels()
        color = Color(255, 255, 255, 10).asConstraint()
    } childOf drawBox

    private val slider = Slider(valueToPercent(prop.getValue()))

    private val minText = UIText(prop.property.min.toString()).constrain {
        x = 8.pixels(alignOutside = true) // RelativeConstraint(1.25f) - (prop.property.max.toString().width() / 2).pixels()
        y = CenterConstraint()
        color = Color(255, 255, 255, 10).asConstraint()
    } childOf slider

    private val maxText = UIText(prop.property.max.toString()).constrain {
        x = 8.pixels(alignOpposite = true, alignOutside = true) // RelativeConstraint(2.25f) + (prop.property.max.toString().width() / 2).coerceAtLeast(15).pixels()
        y = CenterConstraint()
        color = Color(255, 255, 255, 10).asConstraint()
    } childOf slider

    private val currentText = (UIText(prop.getValue<Int>().toString()).constrain {
        x = CenterConstraint().to(slider.knob) as XConstraint
        y = 2.pixels(true).alignOutside(true).to(slider.knob) as YConstraint
        color = Color(255, 255, 255, 10).asConstraint()
    } childOf slider) as UIText

    init {
        slider.onUpdate { value ->
            val tmp = (prop.property.min + ((prop.property.max - prop.property.min) * value)).toInt()
            prop.setValue(tmp)
        }

        onKeyType { _, keyCode ->
            if (!slider.knob.isHovered()) return@onKeyType

            if (keyCode == Keyboard.KEY_LEFT) {
                val newValue = prop.getValue<Int>() - 1

                if (newValue < prop.property.min) return@onKeyType

                slider.setValue(valueToPercent(newValue))
                prop.setValue(newValue)
                currentText.setText(newValue.toString())
            } else if (keyCode == Keyboard.KEY_RIGHT) {
                val newValue = prop.getValue<Int>() + 1

                if (newValue > prop.property.max) return@onKeyType

                slider.setValue(valueToPercent(newValue))
                prop.setValue(newValue)
                currentText.setText(newValue.toString())
            }
        }

        slider childOf drawBox
        onMouseDrag { _, _, _ ->
            currentText.setText(prop.getValue<Int>().toString())
        }
    }

    private fun valueToPercent(value: Int): Float {
        return (value.toFloat() - prop.property.min) / (prop.property.max - prop.property.min)
    }

    override fun animateIn() {
        super.animateIn()
        drawBox.constrain { y = 10.pixels() }
        drawBox.animate {
            setYAnimation(Animations.OUT_EXP, 0.5f, 0.pixels())
            setColorAnimation(Animations.OUT_EXP, 0.5f, Color(0, 0, 0, 100).asConstraint())
        }
        title.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color.WHITE.asConstraint()) }
        text.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color.WHITE.asConstraint()) }
        minText.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color.WHITE.asConstraint()) }
        maxText.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color.WHITE.asConstraint()) }
        currentText.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color.WHITE.asConstraint()) }
        slider.fadeIn()
    }

    override fun animateOut() {
        super.animateOut()
        drawBox.constrain { y = 0.pixels() }
        drawBox.animate {
            setYAnimation(Animations.OUT_EXP, 0.5f, (-10).pixels())
            setColorAnimation(Animations.OUT_EXP, 0.5f, Color(0, 0, 0, 0).asConstraint())
        }
        title.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color(255, 255, 255, 10).asConstraint()) }
        text.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color(255, 255, 255, 10).asConstraint()) }
        minText.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color(255, 255, 255, 10).asConstraint()) }
        maxText.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color(255, 255, 255, 10).asConstraint()) }
        currentText.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color(255, 255, 255, 10).asConstraint()) }
        slider.fadeOut()
    }
}