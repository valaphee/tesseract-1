/*
 * MIT License
 *
 * Copyright (c) 2021, Valaphee.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.valaphee.tesseract.util.text

/**
 * @author Kevin Ludwig
 */
object Emoticon {
    const val XboxFaceButtonDown = 0xE000.toChar()
    const val XboxFaceButtonRight = 0xE001.toChar()
    const val XboxFaceButtonLeft = 0xE002.toChar()
    const val XboxFaceButtonUp = 0xE003.toChar()
    const val XboxBumperLeft = 0xE004.toChar()
    const val XboxBumperRight = 0xE005.toChar()
    const val XboxTriggerLeft = 0xE006.toChar()
    const val XboxTriggerRight = 0xE007.toChar()
    const val XboxSelect = 0xE008.toChar()
    const val XboxStart = 0xE009.toChar()
    const val XboxStickLeft = 0xE00A.toChar()
    const val XboxStickRight = 0xE00B.toChar()
    const val XboxDpadUp = 0xE00C.toChar()
    const val XboxDpadLeft = 0xE00D.toChar()
    const val XboxDpadDown = 0xE00E.toChar()
    const val XboxDpadRight = 0xE00F.toChar()
    const val TipFaceButtonDown = 0xE010.toChar()
    const val TipFaceButtonRight = 0xE011.toChar()
    const val TipFaceButtonLeft = 0xE012.toChar()
    const val TipFaceButtonUp = 0xE013.toChar()
    const val Ps4FaceButtonDown = 0xE020.toChar()
    const val Ps4FaceButtonRight = 0xE021.toChar()
    const val Ps4FaceButtonLeft = 0xE022.toChar()
    const val Ps4FaceButtonUp = 0xE023.toChar()
    const val Ps4BumperLeft = 0xE024.toChar()
    const val Ps4BumperRight = 0xE025.toChar()
    const val Ps4TriggerLeft = 0xE026.toChar()
    const val Ps4TriggerRight = 0xE027.toChar()
    const val Ps4Select = 0xE028.toChar()
    const val Ps4Start = 0xE029.toChar()
    const val Ps4StickLeft = 0xE02A.toChar()
    const val Ps4StickRight = 0xE02B.toChar()
    const val Ps4DpadUp = 0xE02C.toChar()
    const val Ps4DpadLeft = 0xE02D.toChar()
    const val Ps4DpadDown = 0xE02E.toChar()
    const val Ps4DpadRight = 0xE02F.toChar()
    const val SwitchFaceButtonDown = 0xE040.toChar()
    const val SwitchFaceButtonRight = 0xE041.toChar()
    const val SwitchFaceButtonLeft = 0xE042.toChar()
    const val SwitchFaceButtonUp = 0xE043.toChar()
    const val SwitchBumperLeft = 0xE044.toChar()
    const val SwitchBumperRight = 0xE045.toChar()
    const val SwitchTriggerLeft = 0xE046.toChar()
    const val SwitchTriggerRight = 0xE047.toChar()
    const val SwitchSelect = 0xE048.toChar()
    const val SwitchStart = 0xE049.toChar()
    const val SwitchStickLeft = 0xE04A.toChar()
    const val SwitchStickRight = 0xE04B.toChar()
    const val SwitchDpadUp = 0xE04C.toChar()
    const val SwitchDpadLeft = 0xE04D.toChar()
    const val SwitchDpadDown = 0xE04E.toChar()
    const val SwitchDpadRight = 0xE04F.toChar()
    const val MouseLeftButton = 0xE060.toChar()
    const val MouseRightButton = 0xE061.toChar()
    const val MouseMiddleButton = 0xE062.toChar()
    const val MouseButton = 0xE063.toChar()
    const val TipTouchForward = 0xE065.toChar()
    const val TipTouchLeft = 0xE066.toChar()
    const val TipTouchBack = 0xE067.toChar()
    const val TipTouchRight = 0xE068.toChar()
    const val TipTouchJump = 0xE069.toChar()
    const val TipTouchSneak = 0xE06A.toChar()
    const val TipTouchInventory = 0xE06B.toChar()
    const val TipTouchFlyUp = 0xE06C.toChar()
    const val TipTouchFlyDown = 0xE06D.toChar()
    const val TipTouchLeftTrigger = 0xE06E.toChar()
    const val TipTouchRightTrigger = 0xE06F.toChar()
    const val TipLightMouseLeftButton = 0xE070.toChar()
    const val TipLightMouseRightButton = 0xE071.toChar()
    const val TipLightMouseMiddleButton = 0xE072.toChar()
    const val TipLightMouseButton = 0xE073.toChar()
    const val TipLeftStick = 0xE075.toChar()
    const val TipRightStick = 0xE076.toChar()
    const val TouchForward = 0xE080.toChar()
    const val TouchLeft = 0xE081.toChar()
    const val TouchBack = 0xE082.toChar()
    const val TouchRight = 0xE083.toChar()
    const val TouchJump = 0xE084.toChar()
    const val TouchSneak = 0xE085.toChar()
    const val TouchFlyUp = 0xE086.toChar()
    const val TouchFlyDown = 0xE087.toChar()
    const val CraftableToggleOn = 0xE0A0.toChar()
    const val CraftableToggleOff = 0xE0A1.toChar()
    const val WindowsMrLeftGrab = 0xE0C0.toChar()
    const val WindowsMrRightGrab = 0xE0C1.toChar()
    const val WindowsMrMenu = 0xE0C2.toChar()
    const val WindowsMrLeftStick = 0xE0C3.toChar()
    const val WindowsMrRightStick = 0xE0C4.toChar()
    const val WindowsMrLeftTouchpad = 0xE0C5.toChar()
    const val WindowsMrLeftTouchpadHorizontal = 0xE0C6.toChar()
    const val WindowsMrLeftTouchpadVertical = 0xE0C7.toChar()
    const val WindowsMrRightTouchpad = 0xE0C8.toChar()
    const val WindowsMrRightTouchpadHorizontal = 0xE0C9.toChar()
    const val WindowsMrRightTouchpadVertical = 0xE0Ca.toChar()
    const val WindowsMrLeftTrigger = 0xE0CB.toChar()
    const val WindowsMrRightTrigger = 0xE0CC.toChar()
    const val WindowsMrWindows = 0xE0CD.toChar()
    const val Rift0 = 0xE0E0.toChar()
    const val RiftA = 0xE0E1.toChar()
    const val RiftB = 0xE0E2.toChar()
    const val RiftLeftGrab = 0xE0E3.toChar()
    const val RiftRightGrab = 0xE0E4.toChar()
    const val RiftLeftStick = 0xE0E5.toChar()
    const val RiftRightStick = 0xE0E6.toChar()
    const val RiftLeftTrigger = 0xE0E7.toChar()
    const val RiftRightTrigger = 0xE0E8.toChar()
    const val RiftX = 0xE0E9.toChar()
    const val RiftY = 0xE0EA.toChar()
    const val Shank = 0xE100.toChar()
    const val Armor = 0xE101.toChar()
    const val Minecoin = 0xE102.toChar()
    const val Token = 0xE102.toChar()
    const val CodeBuilderButton = 0xE103.toChar()
    const val ImmersiveReaderButton = 0xE104.toChar()
    const val Nbsp = 0xE0FF.toChar()
}
