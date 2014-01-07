/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - 2010 - Organization for Free and Open Source Software,  
 *                Athens, Greece.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package eu.sqooss.service.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.NaturalId;

/**
 * An object that encapsulates a single configuration option.
 * Includes methods to get the values for a specific project.
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 *
 */
@XmlRootElement(name="config-option")
@Entity
@Table(name="CONFIG_OPTION")
public class ConfigurationOption extends DAObject {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="CONFIG_OPTION_ID")
	private long id;
	
	@NaturalId
	@Column(name="CONFIG_KEY")
	@XmlElement
	private String key;
	
	@Column(name="CONFIG_DESCR")
	@XmlElement
	private String description;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="confOpt", cascade=CascadeType.ALL)
	public Set<StoredProjectConfig> configurations;
	
    public ConfigurationOption() {}
	
	public ConfigurationOption(String key, String description) {
		this.key = key;
		this.description = description;
	}

	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
    public Set<StoredProjectConfig> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(Set<StoredProjectConfig> configurations) {
        this.configurations = configurations;
    }

    /**
     * Set an array of values for this configuration option for the specified
     * project.
     * 
     * @param sp The project to add the configuration value
     * @param value The value to set
     * @param overwrite If the key already has a value in the config database,
     *  the method determines whether to overwrite the value or append the
     *  provided value to the existing list of values.
     */
	public void setValues(DBService dbs, StoredProject sp, List<String> values,
			boolean overwrite) {
		String paramProject = "paramProject";
		String paramConfOpt = "paramConfOpt";
		
		StringBuilder query = new StringBuilder();
		query.append(" select spc ");
		query.append(" from StoredProjectConfig spc,");
		query.append("      ConfigurationOption co ");
		query.append(" where spc.confOpt = co ");
		query.append(" and spc.project =:").append(paramProject);
		query.append(" and spc.confOpt =:").append(paramConfOpt);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(paramProject, sp);
		params.put(paramConfOpt, this);

		@SuppressWarnings("unchecked")
		List<StoredProjectConfig> curValues =
				(List<StoredProjectConfig>) dbs.doHQL(query.toString(),params);
		
		assert curValues.size() <= 1 : "At most one StoredProjectConfig should exist for a project and option combination.";
		
		if (overwrite) {
			dbs.deleteRecords(curValues);
			curValues.clear();
		}
		
		StoredProjectConfig spc;
		if(curValues.isEmpty()) {
			spc = new StoredProjectConfig(this, new HashSet<String>(values), sp);
			dbs.addRecord(spc);
		} else {
			curValues.get(0).getValues().addAll(values);
		}
		
	}
	
	/**
	 * Get the configured values for a project.
	 * @param sp The project to retrieve the configuration values for
	 * @return A list of configuration values that correspond to the provided
	 * project
	 */
	public List<String> getValues(DBService dbs, StoredProject sp) {
		String paramProject = "paramProject";
		String paramConfOpt = "paramConfOpt";
		
		StringBuilder query = new StringBuilder();
		query.append(" select spc ");
		query.append(" from StoredProjectConfig spc, ConfigurationOption co ");
		query.append(" where spc.confOpt = co ");
		query.append(" and spc.project =:").append(paramProject);
		query.append(" and spc.confOpt =:").append(paramConfOpt);
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(paramProject, sp);
		params.put(paramConfOpt, this);
		
		@SuppressWarnings("unchecked")
		List<StoredProjectConfig> spcs = (List<StoredProjectConfig>)dbs.doHQL(query.toString(), params);
		
		assert spcs.size() <= 1 : "At most one StoredProjectConfig should exist for a project and option combination.";
		
		if(spcs.size() == 0) {
			return null;
		} else {
			return new ArrayList<String>(spcs.get(0).getValues());
		}
	}
	
	public static ConfigurationOption fromKey(DBService dbs, String key) {
		String paramKey = "key";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(paramKey, key);
		
		List<ConfigurationOption> opts =  dbs.findObjectsByProperties(ConfigurationOption.class, params);
		
		if (opts.isEmpty())
			return null;
		
		return opts.get(0);
	}
	
	@Override
	public String toString() {
		return key + " - " + description; 
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ConfigurationOption
				&& ((ConfigurationOption) obj).getKey().equals( getKey() );
	}

	@Override
	public int hashCode() {
		return getKey().hashCode();
	}
}
