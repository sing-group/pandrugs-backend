/*
 * #%L
 * PanDrugsDB Backend
 * %%
 * Copyright (C) 2015 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package es.uvigo.ei.sing.pandrugsdb.util;

import static es.uvigo.ei.sing.pandrugsdb.util.DigestUtils.md5Digest;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class DigestUtilsTest {

	@Parameters(name = "{0}")
	public static Collection<Object[]> parameters() {
		return asList(new Object[][] {
			{ "hello", "5d41402abc4b2a76b9719d911017c592" },
			{ "world", "7d793037a0760186574b0282f2f435e7" },
			{ "with spaces", "00810dceb4dd615eb5033894660bb01a" },
			{ "numb3r5", "7b52c18324244f795c7c5f5572003e11" },
			{ "#special-characters", "53dc5cf6198474020638c8ca8ba83f5f" },
			{ "CaSeChAnGe", "5d77cd6d83b0f53d64ee1aabd1b195b8" },
			{ "W17h Ev3ry7h1n6!", "5270e72d62ae5335713b24ad2d7761bb" }
		});
	}
	
	@Parameter(0)
	public String word;
	
	@Parameter(1)
	public String md5Word;
	
	@Test
	public void testMd5Digest() {
		assertThat(md5Digest(word), is(equalToIgnoringCase(md5Word)));
	}
	
	//TODO: Move to an independent test case?
	@Test(expected = NullPointerException.class)
	public void testMd5DigestNull() {
		md5Digest(null);
	}
}
