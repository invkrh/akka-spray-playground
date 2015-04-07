package me.invkrh.cmdchat

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 4/7/15
 * Time: 9:29 PM
 */

object Printer {

  def prompt()(implicit name: String) = {
    s"me ($name) > "
  }

  //TODO: better name these 3 display methods

  // cursor in the next line
  def cmdResultDisplay(txt: String, prompt: String = "") = {
    print(s"\n[ $txt ]\n\n" + prompt)
  }

  // cursor in the current line
  def notification(sdr: String, txt: String, prompt: String = "") = {
    print(s"\n\n[ $sdr > $txt ]\n\n" + prompt)
  }

  // cursor in the current line
  def incomingMSGDisplay(sdr: String, txt: String, prompt: String = "") = {
    print(s"\n|\n|\t\t\t\t\t\t\t\t\t\t[ $sdr > $txt ]\n|\n" + prompt)
  }
}
