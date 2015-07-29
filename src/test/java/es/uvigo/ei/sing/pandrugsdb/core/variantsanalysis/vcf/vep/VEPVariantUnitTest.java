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
package es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.vcf.vep;

import static es.uvigo.ei.sing.pandrugsdb.TestUtils.asList;
import static es.uvigo.ei.sing.pandrugsdb.TestUtils.asSet;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.MockType;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EasyMockRunner.class)
public class VEPVariantUnitTest {

	@Mock
	private VEPMetaData metadata;
	
	@Test
	public void testGetCSQAttribute() {
		/* VEPVariant(String sequenceName, long position,
			String referenceAllele, Set<String> alternativeAlleles,
			VEPMetaData metadata) { */
		
		//prepare metadata mock...
		
		expect(metadata.getCSQAttributes()).andReturn(
				asList("Allele", "Gene", "Feature"));
		expectLastCall().anyTimes();
		
		expect(metadata.getInfoAttributeNumber("CSQ")).andReturn(".");
		
		EasyMock.replay(metadata);
		
		VEPVariant variant = new VEPVariant("chr1", 10l, "A", asSet("G"), metadata);
		variant.putInfoValue("CSQ", asList("T||ENSR00000528857"));
		
		
		
		assertThat(variant.getCSQAttribute(0, "Allele"), is("T"));
		assertThat(variant.getCSQAttribute(0, "Gene"), is(nullValue()));
		assertThat(variant.getCSQAttribute(0, "Feature"), is("ENSR00000528857"));
	}
}
