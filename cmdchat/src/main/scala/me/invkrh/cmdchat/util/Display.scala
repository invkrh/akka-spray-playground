package me.invkrh.cmdchat.util

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 4/7/15
 * Time: 9:29 PM
 */

trait Display {

  def getPrompt(name: String) = {
    s"me ($name) > "
  }

  // cursor in the next line
  def response(txt: String, prompt: String = "") = {
    print(s"\n[ $txt ]\n\n" + prompt)
  }

  // cursor in the current line
  def notification(sdr: String, txt: String, prompt: String = "") = {
    print(s"\n\n[ $sdr > $txt ]\n\n" + prompt)
  }

  // cursor in the current line
  def incomingMsg(sdr: String, txt: String, prompt: String = "") = {
    print(s"\n|\n|\t\t\t\t\t\t\t\t\t\t[ $sdr > $txt ]\n|\n" + prompt)
  }

}
