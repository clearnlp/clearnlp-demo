/**
 * Copyright (c) 2009/09-2012/08, Regents of the University of Colorado
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/**
 * Copyright 2012/09-2013/04, University of Massachusetts Amherst
 * Copyright 2013/05-Present, IPSoft Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.clearnlp.demo;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;

import com.clearnlp.component.AbstractComponent;
import com.clearnlp.component.util.CSenTypeClassifierEN;
import com.clearnlp.dependency.DEPNode;
import com.clearnlp.dependency.DEPTree;
import com.clearnlp.nlp.NLPGetter;
import com.clearnlp.nlp.NLPMode;
import com.clearnlp.reader.AbstractReader;
import com.clearnlp.segmentation.AbstractSegmenter;
import com.clearnlp.util.UTArray;

/**
 * @since 1.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class DemoCSenTypeClassifierEN
{
	public DemoCSenTypeClassifierEN(String inputFile, String outputFile) throws Exception
	{
		final String language = AbstractReader.LANG_EN;
		final String modelType = "general-en";
//		final String modelType = "medical-en";
		
		AbstractSegmenter segmenter	 = NLPGetter.getSegmenter(language, NLPGetter.getTokenizer(language));
		AbstractComponent tagger     = NLPGetter.getComponent(modelType, language, NLPMode.MODE_POS);
		AbstractComponent parser     = NLPGetter.getComponent(modelType, language, NLPMode.MODE_DEP);
		CSenTypeClassifierEN typer	 = new CSenTypeClassifierEN();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
		PrintStream fout = new PrintStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
		
		process(reader, fout, segmenter, tagger, parser, typer);
		
		reader.close();
		fout.close();
	}
	
	public void process(BufferedReader reader, PrintStream fout, AbstractSegmenter segmenter, AbstractComponent tagger, AbstractComponent parser, CSenTypeClassifierEN typer)
	{
		DEPTree tree;
		
		for (List<String> tokens : segmenter.getSentences(reader))
		{
			tree = NLPGetter.toDEPTree(tokens);		// put tokens into dependency tree
			tagger.process(tree);					// part-of-speech tagging
			parser.process(tree);					// dependency parsing
			tree.setDependents();
			
			for (DEPNode root : tree.getRoots())
			{
				if (typer.isInterrogative(root))
				{
					fout.println(UTArray.join(tokens, " ")+"\n");
					fout.println(tree.toStringDEP()+"\n");
					break;
				}
			}
		}
	}
	
	static public void main(String[] args)
	{
		try
		{
			new DemoCSenTypeClassifierEN(args[0], args[1]);
		}
		catch (Exception e) {e.printStackTrace();}
	}
}
