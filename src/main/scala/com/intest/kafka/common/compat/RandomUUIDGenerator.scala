package com.intest.kafka.common.compat

import java.util.UUID

import org.apache.commons.math3.random.MersenneTwister

import org.apache.spark.unsafe.types.UTF8String

/**
 * This class is used to generate a UUID from Pseudo-Random Numbers.
 *
 * For the algorithm, see RFC 4122: A Universally Unique IDentifier (UUID) URN Namespace,
 * section 4.4 "Algorithms for Creating a UUID from Truly Random or Pseudo-Random Numbers".
 */
case class RandomUUIDGenerator(randomSeed: Long) {
  private val random = new MersenneTwister(randomSeed)

  def getNextUUID(): UUID = {
    val mostSigBits = (random.nextLong() & 0xFFFFFFFFFFFF0FFFL) | 0x0000000000004000L
    val leastSigBits = (random.nextLong() | 0x8000000000000000L) & 0xBFFFFFFFFFFFFFFFL

    new UUID(mostSigBits, leastSigBits)
  }

  def getNextUUIDUTF8String(): UTF8String = UTF8String.fromString(getNextUUID().toString())
}
