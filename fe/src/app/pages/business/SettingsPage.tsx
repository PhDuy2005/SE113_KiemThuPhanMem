import { useState, useEffect } from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '../../components/ui/card';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '../../components/ui/table';
import { Button } from '../../components/ui/button';
import { Input } from '../../components/ui/input';
import { useGetShippingFees, useUpdateShippingFees } from '../../../dataHook/shippingFeeDataHook';
import { formatCurrency } from '../../../utils/format';
import { toast } from 'sonner';
import { 
  Settings, 
  Truck, 
  Search, 
  Save, 
  Loader2, 
  Info,
  MapPin,
  RefreshCw,
  Coins
} from 'lucide-react';

export function SettingsPage() {
  const { data: serverFees = [], isLoading, refetch, isRefetching } = useGetShippingFees();
  const updateMutation = useUpdateShippingFees();

  const [searchTerm, setSearchTerm] = useState('');
  const [localFees, setLocalFees] = useState<Record<string, number>>({});
  const [hasChanges, setHasChanges] = useState(false);
  const [bulkValue, setBulkValue] = useState('');

  // Sync server fees with local editable state
  useEffect(() => {
    if (serverFees.length > 0) {
      const state: Record<string, number> = {};
      serverFees.forEach(fee => {
        state[fee.provinceCode] = fee.shippingFee;
      });
      setLocalFees(state);
      setHasChanges(false);
    }
  }, [serverFees]);

  const handleFeeChange = (provinceCode: string, value: string) => {
    const num = parseFloat(value);
    if (isNaN(num) || num < 0) return;
    
    setLocalFees(prev => {
      const updated = { ...prev, [provinceCode]: num };
      // Check if actually modified
      const original = serverFees.find(f => f.provinceCode === provinceCode)?.shippingFee;
      if (updated[provinceCode] !== original) {
        setHasChanges(true);
      }
      return updated;
    });
  };

  const handleBulkApply = () => {
    const num = parseFloat(bulkValue);
    if (isNaN(num) || num < 0) {
      toast.error('Please enter a valid positive number');
      return;
    }

    setLocalFees(prev => {
      const updated = { ...prev };
      Object.keys(updated).forEach(code => {
        updated[code] = num;
      });
      setHasChanges(true);
      return updated;
    });
    toast.success(`Applied bulk shipping fee of ${formatCurrency(num)} to all provinces.`);
  };

  const handleSave = () => {
    const payload = serverFees.map(fee => ({
      provinceCode: fee.provinceCode,
      province: fee.province,
      shippingFee: (localFees[fee.provinceCode] ?? fee.shippingFee).toFixed(2)
    }));

    updateMutation.mutate(payload, {
      onSuccess: () => {
        toast.success('Shipping fee configurations updated successfully');
        setHasChanges(false);
      },
      onError: (err: any) => {
        toast.error(err?.message || 'Failed to save shipping fees');
      }
    });
  };

  const filteredFees = serverFees.filter(fee => 
    fee.province.toLowerCase().includes(searchTerm.toLowerCase()) ||
    fee.provinceCode.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">System Settings</h1>
          <p className="text-muted-foreground">Configure logistics policies and provincial shipping rates</p>
        </div>
      </div>

      {/* Info Notice Banner */}
      <Card className="bg-primary/5 border-primary/20">
        <CardContent className="p-4 flex gap-4">
          <Info className="h-5 w-5 text-primary shrink-0 mt-0.5" />
          <div className="text-sm">
            <p className="font-semibold text-primary">Logistics Configuration Notice</p>
            <p className="text-muted-foreground mt-0.5">
              These values represent direct base rates charged to customers during checkout depending on their shipping addresses. 
              Always review changes carefully before saving to prevent checkout errors or lost logistics revenue.
            </p>
          </div>
        </CardContent>
      </Card>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Bulk Modifier & Stats */}
        <div className="space-y-6 lg:col-span-1">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2 text-sm uppercase font-black tracking-wider text-muted-foreground">
                <Truck className="h-4 w-4 text-primary" /> Active Logistics Overview
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex justify-between items-center text-sm border-b pb-2">
                <span className="text-muted-foreground">Configured Provinces</span>
                <span className="font-bold">{serverFees.length} regions</span>
              </div>
              <div className="flex justify-between items-center text-sm border-b pb-2">
                <span className="text-muted-foreground">Average Shipping Cost</span>
                <span className="font-bold">
                  {serverFees.length > 0 
                    ? formatCurrency(serverFees.reduce((acc, f) => acc + f.shippingFee, 0) / serverFees.length) 
                    : '$0.00'
                  }
                </span>
              </div>
            </CardContent>
          </Card>

          <Card className="border-primary/10">
            <CardHeader>
              <CardTitle className="flex items-center gap-2 text-sm uppercase font-black tracking-wider text-muted-foreground">
                <Coins className="h-4 w-4 text-primary" /> Bulk Adjust Rates
              </CardTitle>
              <CardDescription>
                Quickly override all regional shipping rates in one go.
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <label className="text-xs font-bold uppercase tracking-wider text-muted-foreground">New Universal Base Rate ($)</label>
                <div className="flex gap-2">
                  <Input
                    type="number"
                    value={bulkValue}
                    min={0}
                    onChange={(e) => setBulkValue(e.target.value)}
                    placeholder="e.g. 15.00"
                    className="flex-1"
                  />
                  <Button variant="secondary" onClick={handleBulkApply} className="shrink-0">
                    Apply All
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Detailed Province Config List */}
        <div className="lg:col-span-2 space-y-4">
          <div className="flex flex-col sm:flex-row gap-3 justify-between items-stretch sm:items-center">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
              <Input
                placeholder="Search provinces by name or postal code..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-9 bg-card"
              />
            </div>
            <div className="flex items-center gap-2">
              <Button 
                variant="outline" 
                size="icon"
                onClick={() => refetch()}
                disabled={isLoading || isRefetching}
                title="Refresh"
                className="h-10 w-10 rounded-xl shrink-0"
              >
                <RefreshCw className={`h-4 w-4 ${isRefetching ? 'animate-spin' : ''}`} />
              </Button>
              <Button 
                onClick={handleSave} 
                disabled={!hasChanges || updateMutation.isPending}
                className="shadow-md h-10 px-4 rounded-xl flex items-center gap-2 shrink-0"
              >
                {updateMutation.isPending ? (
                  <Loader2 className="h-4 w-4 animate-spin" />
                ) : (
                  <Save className="h-4 w-4" />
                )}
                Save Changes
              </Button>
            </div>
          </div>

          {isLoading ? (
            <Card className="flex h-96 flex-col items-center justify-center text-center">
              <Loader2 className="h-8 w-8 animate-spin text-primary mb-2" />
              <p className="text-sm text-muted-foreground uppercase font-bold tracking-widest text-[10px]">Loading provincial rates...</p>
            </Card>
          ) : filteredFees.length === 0 ? (
            <Card className="flex h-96 flex-col items-center justify-center text-center p-6">
              <MapPin className="h-12 w-12 text-muted-foreground mb-4 opacity-50" />
              <p className="text-lg font-bold">No Provinces Found</p>
              <p className="text-muted-foreground text-sm">No regions match your search criteria. Try adjusting your filters.</p>
            </Card>
          ) : (
            <Card>
              <CardContent className="p-0">
                <div className="max-h-[60vh] overflow-y-auto">
                  <Table>
                    <TableHeader className="bg-muted/30 sticky top-0 z-10">
                      <TableRow>
                        <TableHead className="w-1/3">Province / Code</TableHead>
                        <TableHead className="w-1/3 text-right">Current Rate</TableHead>
                        <TableHead className="w-1/3 text-right">Editable Fee ($)</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {filteredFees.map(fee => (
                        <TableRow key={fee.provinceCode} className="hover:bg-muted/10">
                          <TableCell>
                            <div className="font-semibold">{fee.province}</div>
                            <div className="text-xs text-muted-foreground font-mono">{fee.provinceCode}</div>
                          </TableCell>
                          <TableCell className="text-right font-medium">
                            {formatCurrency(fee.shippingFee)}
                          </TableCell>
                          <TableCell className="text-right">
                            <div className="flex justify-end items-center gap-2">
                              <span className="text-muted-foreground text-xs">$</span>
                              <input
                                type="number"
                                min={0}
                                step="0.01"
                                value={localFees[fee.provinceCode] !== undefined ? localFees[fee.provinceCode] : fee.shippingFee}
                                onChange={(e) => handleFeeChange(fee.provinceCode, e.target.value)}
                                className="w-24 h-9 text-right text-xs font-bold border border-border rounded-xl bg-card text-foreground focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary transition-all px-2"
                              />
                            </div>
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </div>
              </CardContent>
            </Card>
          )}
        </div>
      </div>
    </div>
  );
}
