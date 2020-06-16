package com.haha.libreria;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

public class MetodosGenerales {

    private Context mContext;

    public MetodosGenerales(Context context) {
        this.mContext = context;
    }

    public void LlenarSpinnerSimple(String[] items, Spinner spinner, int posicion) {
        //String[] list_depto = new String[]{"Boaco", "Carazo", "Chinandega", "Chontales", "Esteli", "Granada", "Jinotega", "Leon", "Madriz", "Managua", "Masaya", "Matagalpa", "Nueva Segovia", "RAAN", "RAAS", "Rio San Juan", "Rivas"};
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item, items);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        if (posicion >= 0 && posicion < items.length) {
            spinner.setSelection(posicion, true);
        }
    }

    /**
     * public class Custom_Spn_Lista extends ArrayAdapter<classPdv> {
     *
     *             private final List<classPdv> Pdv;
     *
     *             public Custom_Spn_Lista(List<classPdv> pdv) {
     *                 super(mContext, R.layout.list_pdv_find, pdv);
     *                 this.Pdv = pdv;
     *             }
     *
     *             '@Override
     *             public View getDropDownView(int position, View convertView, ViewGroup parent) {
     *                 return getCustomView(position, convertView, parent);
     *             }
     *
     *             '@Override
     *             public View getView(int position, View convertView, ViewGroup parent) {
     *                 return getCustomView(position, convertView, parent);
     *             }
     *
     *             public View getCustomView(int position, View view, ViewGroup parent) {
     *                 LayoutInflater inflater = mActivity.getLayoutInflater();
     *                 View rowView = inflater.inflate(R.layout.list_pdv_find, null, true);
     *
     *                 try {
     *                     TextView txtpdv = (TextView) rowView.findViewById(R.id.tv_pdv_spnfind);
     *                     txtpdv.setText(Pdv.get(position).getNombre());
     *
     *                 } catch (Exception e) {
     *                     e.printStackTrace();
     *                 }
     *                 return rowView;
     *             }
     *
     *         }
     */
    public void LlenarSpinnerCustomVerEjemplos() {

       /* public class Custom_Spn_Lista extends ArrayAdapter<classPdv> {

            private final List<classPdv> Pdv;

            public Custom_Spn_Lista(List<classPdv> pdv) {
                super(mContext, R.layout.list_pdv_find, pdv);
                this.Pdv = pdv;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                return getCustomView(position, convertView, parent);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return getCustomView(position, convertView, parent);
            }

            public View getCustomView(int position, View view, ViewGroup parent) {
                LayoutInflater inflater = mActivity.getLayoutInflater();
                View rowView = inflater.inflate(R.layout.list_pdv_find, null, true);

                try {
                    TextView txtpdv = (TextView) rowView.findViewById(R.id.tv_pdv_spnfind);
                    txtpdv.setText(Pdv.get(position).getNombre());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return rowView;
            }

        }*/
    }


    public void LlenarListViewSimple(String[] items, ListView listView) {
        //String[] list_depto = new String[]{"Boaco", "Carazo", "Chinandega", "Chontales", "Esteli", "Granada", "Jinotega", "Leon", "Madriz", "Managua", "Masaya", "Matagalpa", "Nueva Segovia", "RAAN", "RAAS", "Rio San Juan", "Rivas"};
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_list_item_1, items);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listView.setAdapter(dataAdapter);
    }

    public void LlenarListViewCustomVerEjemplos() {
        /*
        public static class Custom_Lista extends ArrayAdapter<String> {

            private final Activity context;
            private final List<String> Pdv;

            public Custom_Lista(Activity context, List<String> pdv) {
                super(context, R.layout.list_pdv, pdv);
                this.context = context;
                this.Pdv = pdv;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = getCustomView(position, convertView, parent);
                return view;
            }

            public View getCustomView(int position, View view, ViewGroup parent) {
                LayoutInflater inflater = context.getLayoutInflater();
                View rowView = inflater.inflate(R.layout.list, null, true);

                try {
                    ((TextView) rowView.findViewById(R.id.tv1)).setText(Pdv.get(position));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return rowView;
            }

        }

       forma de  USO
        listviewid.setAdapter(new Custom_Lista(mActivity, Lista1));
        */


        // otro ejemplo ************************************************
        /*
            List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
            dentro del ciclo{
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("key1", "Dato1");
                hm.put("key2", "Dato2");
                aList.add(hm);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(mContext, aList, R.layout.list_pdv, new String[]{"key1", "key2"}, new int[]{R.id.tv1, R.id.tv2});
            listviewId.setAdapter(simpleAdapter);
         */
    }

}
