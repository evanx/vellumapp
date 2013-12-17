/*
 * Source https://code.google.com/p/vellum by @evanxsummers

       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements. See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.  
 */
package vellumexp.hype.java;

/**
 *
 * @author evan.summers
 */
public class JavaMeta {

    public static final String beginPattern = "<pre style=\"java\">";
    public static final String endPattern = "</pre>";

    public static final String[] accessKeywords = {
        "public", "protected", "private"
    };

    public static final String[] classKeywords = {
        "class", "interface", "enum"
    };
    
    public static final String[] keywords = {
        "public", "protected", "private", 
        "abstract", "class", "interface", "extends", "implements",         
        "void", "int", "short", "long", "boolean", 
        "synchronized", "throws",
        "null", "true", "false", 
        "new", "this", "super",
        "return", "throw",         
        "if", "while", "for",
        "try", "catch", "finally"                       
    };

}
