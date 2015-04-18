package me.invkrh.cmdchat.mock

import java.io.{ByteArrayInputStream, SequenceInputStream}

import scala.collection.JavaConversions._

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 4/12/15
 * Time: 12:54 PM
 */

trait MockUserInput {
  def cannedInput[T](cmds: String*)(thunk: => T) = {
    val inputs = cmds.toIterator
      .map(x => new ByteArrayInputStream((x + "\n").getBytes))
    val in = new SequenceInputStream(inputs)
    Console.withIn(in)(thunk)
  }
}
