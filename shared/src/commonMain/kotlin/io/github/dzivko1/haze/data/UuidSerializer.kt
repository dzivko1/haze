package io.github.dzivko1.haze.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.uuid.Uuid

object UuidSerializer : KSerializer<Uuid> {
  override val descriptor = PrimitiveSerialDescriptor("Uuid", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder): Uuid {
    return Uuid.parseHex(decoder.decodeString())
  }

  override fun serialize(encoder: Encoder, value: Uuid) {
    encoder.encodeString(value.toHexString())
  }
}