/* See LICENSE for licensing and NOTICE for copyright. */
package org.passay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Result of a password rule validation.
 *
 * @author  Middleware Services
 */
public class RuleResult
{

  /** Whether password rule was successful. */
  protected boolean valid;

  /** Details associated with a password rule result. */
  protected List<RuleResultDetail> details = new ArrayList<>();

  /** Metadata produced by a password rule. */
  protected RuleResultMetadata metadata = new RuleResultMetadata();


  /** Default constructor. */
  public RuleResult() {}


  /**
   * Creates a new rule result.
   *
   * @param  b  result validity
   */
  public RuleResult(final boolean b)
  {
    setValid(b);
  }


  /**
   * Creates a new rule result.
   *
   * @param  b  result validity
   * @param  rrd  details associated with this result
   */
  public RuleResult(final boolean b, final RuleResultDetail rrd)
  {
    setValid(b);
    details.add(rrd);
  }


  /**
   * Creates a new rule result.
   *
   * @param  b  result validity
   * @param  rrm  metadata associated by the rule with the password
   */
  public RuleResult(final boolean b, final RuleResultMetadata rrm)
  {
    setValid(b);
    setMetadata(rrm);
  }


  /**
   * Creates a new rule result.
   *
   * @param  b  result validity
   * @param  rrd  details associated with this result
   * @param  rrm  metadata associated by the rule with the password
   */
  public RuleResult(final boolean b, final RuleResultDetail rrd, final RuleResultMetadata rrm)
  {
    setValid(b);
    details.add(rrd);
    setMetadata(rrm);
  }


  /**
   * Returns whether the result of the rule verification is a valid password.
   *
   * @return  valid password for this rule
   */
  public boolean isValid()
  {
    return valid;
  }


  /**
   * Sets whether the result of the rule verification is a valid password.
   *
   * @param  b  valid password for this rule
   */
  public void setValid(final boolean b)
  {
    valid = b;
  }


  /**
   * Returns any details associated with the rule verification.
   *
   * @return  rule result details
   */
  public List<RuleResultDetail> getDetails()
  {
    return details;
  }


  /**
   * Sets any details associated with the rule verification.
   *
   * @param  rrd  rule result details
   */
  public void setDetails(final RuleResultDetail... rrd)
  {
    setDetails(Arrays.asList(rrd));
  }


  /**
   * Sets any details associated with the rule verification.
   *
   * @param  rrd  rule result details
   */
  public void setDetails(final List<RuleResultDetail> rrd)
  {
    details = rrd;
  }


  /**
   * Returns metadata associated with the rule verification.
   *
   * @return  rule result metadata
   */
  public RuleResultMetadata getMetadata()
  {
    return metadata;
  }


  /**
   * Sets metadata associated with the rule verification.
   *
   * @param  rrm  rule result metadata
   */
  public void setMetadata(final RuleResultMetadata rrm)
  {
    metadata = rrm;
  }


  @Override
  public String toString()
  {
    return String.format(
      "%s@%h::valid=%s,details=%s,metadata=%s",
      getClass().getName(),
      hashCode(),
      valid,
      details,
      metadata);
  }
}
