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
package org.jboss.shrinkwrap.impl.descriptors;

import java.util.HashMap;
import java.util.Map;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.descriptors.DescriptiveArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.impl.base.AssignableBase;
import org.jboss.shrinkwrap.impl.descriptors.TypeDescriptor.ArchiveType;

/**
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class DescriptiveArchiveImpl extends AssignableBase<Archive<?>> implements DescriptiveArchive {

    private Map<TypeDescriptor, String> mappings = new HashMap<TypeDescriptor, String>();
    {
        mappings.put(new TypeDescriptor("web.xml", ArchiveType.WebArchive), "WEB-INF/");
        mappings.put(new TypeDescriptor("beans.xml", ArchiveType.WebArchive), "WEB-INF/");
        mappings.put(new TypeDescriptor("beans.xml", ArchiveType.JavaArchive), "META-INF/");
        mappings.put(new TypeDescriptor("application.xml", ArchiveType.EnterpriseArchive), "META-INF/");
        mappings.put(new TypeDescriptor("persistence.xml", ArchiveType.WebArchive), "WEB-INF/classes/META-INF/");
        mappings.put(new TypeDescriptor("persistence.xml", ArchiveType.JavaArchive), "META-INF/");

        mappings.put(new TypeDescriptor("ra.xml", ArchiveType.ResourceAdapterArchive), "META-INF/");
        mappings.put(new TypeDescriptor("ejb-jar.xml", ArchiveType.JavaArchive), "META-INF/");
        mappings.put(new TypeDescriptor("ejb-jar.xml", ArchiveType.WebArchive), "WEB-INF/");
        mappings.put(new TypeDescriptor("taglib.xml", ArchiveType.WebArchive), "WEB-INF/");

        mappings.put(new TypeDescriptor("faces-config.xml", ArchiveType.WebArchive), "WEB-INF/");
        mappings.put(new TypeDescriptor("taglibrary.tld", ArchiveType.WebArchive), "WEB-INF/");

        mappings.put(new TypeDescriptor("web-fragment.xml", ArchiveType.WebArchive), "WEB-INF/"); // ??
        mappings.put(new TypeDescriptor("web-fragment.xml", ArchiveType.JavaArchive), "META-INF/");
    }

    public DescriptiveArchiveImpl(Archive<?> archive) {
        super(archive);
    }

    @Override
    public DescriptiveArchive add(Descriptor descriptor) {
        ArchiveType type = findType();
        if(type == null) {
            throw new IllegalArgumentException("Archive type not supported: " + getArchive().getName());
        }

        TypeDescriptor location = new TypeDescriptor(descriptor.getDescriptorName(), type);
        String mapping = mappings.get(location);
        if(mapping == null) {
            throw new IllegalArgumentException("Could not find mapping for " + location);
        }

        ArchivePath descriptorPath = ArchivePaths.create(mapping, descriptor.getDescriptorName());
        getArchive().add(new StringAsset(descriptor.exportAsString()), descriptorPath);
        return this;
    }

    @Override
    public <T extends Descriptor> T get(Class<T> descriptorType) {
        ArchiveType type = findType();
        if(type == null) {
            throw new IllegalArgumentException("Archive type not supported: " + getArchive().getName());
        }

        T emptyType = Descriptors.create(descriptorType);
        TypeDescriptor location = new TypeDescriptor(emptyType.getDescriptorName(), type);
        String mapping = mappings.get(location);
        if(mapping == null) {
            throw new IllegalArgumentException("Could not find mapping for " + location);
        }

        ArchivePath descriptorPath = ArchivePaths.create(mapping, emptyType.getDescriptorName());
        if(getArchive().contains(descriptorPath)) {
            return Descriptors.importAs(descriptorType).fromStream(getArchive().get(descriptorPath).getAsset().openStream(), true);
        }

        return emptyType;
    }

    private ArchiveType findType() {
        for(ArchiveType type : ArchiveType.values()) {
            if(isOfType(type)) {
                return type;
            }
        }
        return null;
    }

    private boolean isOfType(ArchiveType type) {
        if(getArchive().getName().endsWith(type.getExtension())) {
            return true;
        }
        return false;
    }
}