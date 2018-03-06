/*==============================================================================
 *
 * COPYRIGHT
 *
 * COMPASS SECURITY AG
 * Werkstrasse 20
 * CH-8645 Jona
 * SWITZERLAND
 *
 * www.compass-security.com
 * info@compass-security.com
 *
 * tel int + 41 55 214 41 60
 *
 * The copyright to the computer program(s) herein
 * is the property of Compass Security AG, Switzerland.
 * The program(s) may be used and/or copied only with
 * the written permission of Compass Security AG or in accordance with
 * the terms and conditions stipulated in the agreement/contract
 * under which the program(s) have been supplied.
 *==============================================================================
 *
 * Original author : dalleman
 * Creation date   : 05.03.2018
 *
 *==============================================================================
 */

package utils;

public enum EventEnum
{
  Birthday(1),
  Death(0);

  private final int value;

  private EventEnum(
                    int value)
  {
    this.value = value;
  }

  public int getValue()
  {
    return value;
  }

}
