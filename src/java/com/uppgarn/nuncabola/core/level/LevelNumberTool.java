/*
 * LevelNumberTool.java
 *
 * Copyright (c) 2003-2020 Nuncabola authors
 * See authors.txt for details.
 *
 * Nuncabola is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 */

package com.uppgarn.nuncabola.core.level;

import com.uppgarn.codelibf.util.*;

import java.util.*;

public final class LevelNumberTool {
  private static final String[] ROMAN_NUMERALS = {
    "I",   "II",   "III",   "IV",   "V",
    "VI",  "VII",  "VIII",  "IX",   "X",
    "XI",  "XII",  "XIII",  "XIV",  "XV",
    "XVI", "XVII", "XVIII", "XIX",  "XX",
    "XXI", "XXII", "XXIII", "XXIV", "XXV"
  };
  
  private static String getNormalNumber(int idx) {
    return StringTool.prePad(Integer.toString(idx + 1), 2, '0');
  }
  
  private static String getBonusNumber(int idx) {
    return ROMAN_NUMERALS[idx];
  }
  
  public static List<String> getNumbers(List<Level> levels) {
    List<String> numbers = new ArrayList<>(levels.size());
    
    int normalCount = 0;
    int bonusCount  = 0;
    
    for (Level level: levels) {
      String number;
      
      if (level == null) {
        number = null;
      } else if (!level.isBonus()) {
        number = getNormalNumber(normalCount++);
      } else {
        number = getBonusNumber (bonusCount ++);
      }
      
      numbers.add(number);
    }
    
    return numbers;
  }
  
  private LevelNumberTool() {
  }
}
