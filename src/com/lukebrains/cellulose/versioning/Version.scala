/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.lukebrains.cellulose.versioning

import java.io.{ObjectOutputStream, ObjectInputStream, FileOutputStream, FileInputStream}
import java.nio.file.{Files, Paths}
// import scala.collection.immutable.HashMap
import java.util.HashMap
import java.io.IOException

class Version {
  private var cache = new HashMap[Int, Object]
  private var versionLimit : Int = 0
  private var versionID : Int = 0
  
  def initializeVersion(versionLimit : Int) {
    this.versionLimit = versionLimit
    versionID += 1
  }
  
  def cacheConfiguration(configuration : Object, outputDirectory : String) {
    if(cache.size <= versionLimit) {
      cache.put(versionID, configuration)
      versionID += 1
    }
    else {
      val cacheFile = new FileOutputStream(outputDirectory + "/" + versionID)
      val cacheOutputStream = new ObjectOutputStream(cacheFile)
      try {
        versionID += 1
        cacheOutputStream.writeObject(configuration)
        cacheOutputStream.flush()
      } catch {
        case i : IOException => i.printStackTrace()
      } finally {
        cacheOutputStream.close()
      }
    }
  }
  
  def restoreConfiguration(version : Int, inputDirectory : String) : Object = {
    var configuration : Object = null
    if(cache.containsKey(version)) {
      configuration = cache.get(version)
    }
    else {
      if(Files.exists(Paths.get(inputDirectory + "/" + version))) {
        val cacheFile = new FileInputStream(inputDirectory + "/" + version)
        val cacheInputStream = new ObjectInputStream(cacheFile)
        try {
          configuration = cacheInputStream.readObject()
        } catch {
          case i : IOException => i.printStackTrace()
        } finally {
          cacheInputStream.close()
        }
      }
      else
        println("Version " + version + " does not exist.")
    }
    return configuration
  }
  // Will add remove version capability.
}