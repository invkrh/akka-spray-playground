package mock

import scala.collection.JavaConversions._
import java.io.{SequenceInputStream, ByteArrayInputStream}

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 4/12/15
 * Time: 12:54 PM
 */

trait MockUserInput {
  def cannedInput(cmds: String*) = {
    val inputs = cmds.toIterator
      .map(x => new ByteArrayInputStream((x + "\n").getBytes))
    val in = new SequenceInputStream(inputs)
    Console.setIn(in)
  }
}
