/*
 * ADEB - machine learning pipelines made easy Copyright (C) 2014 Isak Karlsson
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.briljantframework.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.briljantframework.dataframe.DataFrame;

/**
 * Created by Isak Karlsson on 14/08/14.
 */
public abstract class DataOutputStream extends FilterOutputStream {

  /**
   * @param out the out
   */
  public DataOutputStream(OutputStream out) {
    super(out);
  }

  /**
   * @param dataFrame the instances
   * @throws IOException the iO exception
   */
  public abstract void write(DataFrame dataFrame) throws IOException;
}
