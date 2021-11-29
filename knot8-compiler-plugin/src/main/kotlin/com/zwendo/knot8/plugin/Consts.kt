package com.zwendo.knot8.plugin

import org.jetbrains.org.objectweb.asm.Opcodes

// Project ASM api version
internal const val API_VERSION: Int = Opcodes.ASM9

// Project group id in bytecode version
internal const val BYTECODE_GROUP: String = "com/zwendo/"

// Project root project in bytecode
internal const val BYTECODE_PROJECT: String = "knot8/"

// Project annotations path in bytecode
internal const val BYTECODE_ANNOTATIONS_PATH: String = BYTECODE_GROUP + BYTECODE_PROJECT + "annotation/"
