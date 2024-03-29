/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bello.ishcodebellz.flooringmasteryapplication.ui;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *
 * @author AHMED ADEYEMI BELLO
 */
public interface UserInputOutput {

    void print(String prompt);

    String formatCurrency(BigDecimal amount);

    String readString(String prompt);

    String readString(String prompt, int max);

    BigDecimal readBigDecimal(String prompt, int scale);

    BigDecimal readBigDecimal(String prompt, int scale, BigDecimal min);

    int readInt(String prompt);

    int readInt(String prompt, int min, int max);

    LocalDate readDate(String prompt);

    LocalDate readDate(String prompt, LocalDate min, LocalDate max);

}
