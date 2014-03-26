/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.shrinkwrap.api.descriptors;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.application5.ApplicationDescriptor;
import org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor;
import org.jboss.shrinkwrap.descriptor.api.persistence10.PersistenceDescriptor;
import org.jboss.shrinkwrap.descriptor.api.webapp25.WebAppDescriptor;
import org.junit.Assert;
import org.junit.Test;

/**
 * DescriptiveArchiveTestCase
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class DescriptiveArchiveTestCase {

    /*
     * beans.xml
     */

    @Test
    public void shouldBeAbleToAddBeansDescriptorToWebArchive() {
        WebArchive archive = create(WebArchive.class, BeansDescriptor.class);

        ArchivePath target = ArchivePaths.create("WEB-INF/beans.xml");
        Assert.assertTrue("should be located in " + target, archive.contains(target));
    }

    @Test
    public void shouldBeAbleToAddBeansDescriptorToJavaArchive() {
        JavaArchive archive = create(JavaArchive.class, BeansDescriptor.class);

        ArchivePath target = ArchivePaths.create("META-INF/beans.xml");
        Assert.assertTrue("should be located in " + target, archive.contains(target));
    }

    /*
     * persistence.xml
     */

    @Test
    public void shouldBeAbleToAddPersistenceDescriptorToWebArchive() {
        WebArchive archive = create(WebArchive.class, PersistenceDescriptor.class);

        ArchivePath target = ArchivePaths.create("WEB-INF/classes/META-INF/persistence.xml");
        Assert.assertTrue("should be located in " + target, archive.contains(target));
    }

    @Test
    public void shouldBeAbleToAddPersistenceDescriptorToJavaArchive() {
        JavaArchive archive = create(JavaArchive.class, PersistenceDescriptor.class);

        ArchivePath target = ArchivePaths.create("META-INF/persistence.xml");
        Assert.assertTrue("should be located in " + target, archive.contains(target));
    }

    /*
     * application.xml
     */

    @Test
    public void shouldBeAbleToAddApplicationDescriptorToEnterpriseArchive() {
        EnterpriseArchive archive = create(EnterpriseArchive.class, ApplicationDescriptor.class);

        ArchivePath target = ArchivePaths.create("META-INF/application.xml");
        Assert.assertTrue("should be located in " + target, archive.contains(target));
    }

    /*
     * web.xml
     */

    @Test
    public void shouldBeAbleToAddWebDescriptorToEnterpriseArchive() {
        WebArchive archive = create(WebArchive.class, WebAppDescriptor.class);

        ArchivePath target = ArchivePaths.create("WEB-INF/web.xml");
        Assert.assertTrue("should be located in " + target, archive.contains(target));
    }

    @Test
    public void shouldBeAbleToGetBeansDescriptorFromWebArchive() {
        String alternativeName = "XYXA";

        WebArchive archive = create(WebArchive.class,
                Descriptors.create(BeansDescriptor.class).getOrCreateAlternatives().clazz(alternativeName).up());

        BeansDescriptor desc = archive.as(DescriptiveArchive.class).get(BeansDescriptor.class);
        Assert.assertTrue("should contain word " + alternativeName, desc.exportAsString().contains(alternativeName));
    }

    @Test
    public void shouldBeAbleToGetBeansDescriptorFromJavaArchive() {
        String alternativeName = "XYXA";

        JavaArchive archive = create(JavaArchive.class,
                Descriptors.create(BeansDescriptor.class).getOrCreateAlternatives().clazz(alternativeName).up());

        BeansDescriptor desc = archive.as(DescriptiveArchive.class).get(BeansDescriptor.class);
        Assert.assertTrue("should contain word " + alternativeName, desc.exportAsString().contains(alternativeName));
    }

    private <T extends Archive<T>, X extends Descriptor> T create(Class<T> archiveType, Class<X> descriptorType) {
        return ShrinkWrap.create(archiveType)
                .as(DescriptiveArchive.class)
                    .add(Descriptors.create(descriptorType))
                .as(archiveType);
    }

    private <T extends Archive<T>, X extends Descriptor> T create(Class<T> archiveType, X descriptor) {
        return ShrinkWrap.create(archiveType)
                .as(DescriptiveArchive.class)
                    .add(descriptor)
                .as(archiveType);
    }
}