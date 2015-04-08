package me.invkrh.cmdchat

/**
 * Created with IntelliJ IDEA.
 * User: invkrh
 * Date: 4/7/15
 * Time: 9:29 PM
 */

object Printer {

  def getPrompt(name: String) = {
    s"me ($name) > "
  }

  // cursor in the next line
  def showInputRes(txt: String, prompt: String = "") = {
    print(s"\n[ $txt ]\n\n" + prompt)
  }

  // cursor in the current line
  def showNotification(sdr: String, txt: String, prompt: String = "") = {
    print(s"\n\n[ $sdr > $txt ]\n\n" + prompt)
  }

  // cursor in the current line
  def showIncomingMSG(sdr: String, txt: String, prompt: String = "") = {
    print(s"\n|\n|\t\t\t\t\t\t\t\t\t\t[ $sdr > $txt ]\n|\n" + prompt)
  }
}
